package com.sparta.market.domain.chat.service;

import com.sparta.market.domain.chat.dto.MessageDto;
import com.sparta.market.domain.chat.entity.ChatMessage;
import com.sparta.market.domain.chat.entity.ChatRoom;
import com.sparta.market.domain.chat.repository.MessageRepository;
import com.sparta.market.domain.chat.repository.RoomRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.security.config.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.sparta.market.global.common.exception.ErrorCode.NOT_EXIST_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate<String, ChatMessage> redisTemplate;

    private static final String MESSAGE_CACHE_KEY = "messageCacheRoom:";

    public void CachedMessage(MessageDto dto, Long roomId, UserDetailsImpl userDetails) {
        User user = findAuthenticatedUser(userDetails);
        ChatRoom chatRoom = roomService.findRoom(roomId);

        ChatMessage chatMessage = ChatMessage.builder()
                .content(dto.getContent())
                .sender(user)
                .chatRoom(chatRoom)
                .sendTime(LocalDateTime.now())
                .build();
        String cacheKey = MESSAGE_CACHE_KEY+roomId;

        redisTemplate.opsForList().rightPush(cacheKey, chatMessage);
    }

    @Scheduled(cron = "0 0 0/1 * * *") /* 1시간 마다*/
    @Transactional
    public void saveMessages() {
        /* 레디스에 캐싱된 채팅방 아이디만 파싱*/
        List<Long> roomIdList = Objects.requireNonNull(redisTemplate.keys(MESSAGE_CACHE_KEY + "*")).stream()
                .map(key -> Long.parseLong(key.substring(MESSAGE_CACHE_KEY.length())))
                .toList();
        /* 각 채팅방의 캐싱된 메세지를 찾아 DB에 저장한 후, 캐싱된 메세지는 삭제*/
        for(Long id : roomIdList) {
            String cacheKey = MESSAGE_CACHE_KEY + id;
            try{
                List<ChatMessage> messages = redisTemplate.opsForList().range(cacheKey, 0, -1);
                if(messages != null && !messages.isEmpty()) {
                    messageRepository.saveAll(messages);
                    redisTemplate.opsForList().trim(cacheKey, messages.size(), -1);
                } else {
                    continue;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public Page<ChatMessage> findMessages(long roomId, int page, int size) {
        String cacheKey = MESSAGE_CACHE_KEY+roomId;
        long start = (long) (page - 1) * size;
        long end = start + size - 1;

        List<ChatMessage> cachedMessages = redisTemplate.opsForList().range(cacheKey, start, end);

        ChatRoom chatRoom = roomService.findRoom(roomId);
        List<ChatMessage> dbMessages = new ArrayList<>();
        /* 캐시된 메세지 수가 요청한 페이지 사이즈보다 적을 경우*/
        assert cachedMessages != null;
        if(cachedMessages.size() < size) {
            // DB에서 가져와야 할 페이지 수
            int dbPage = page - cachedMessages.size()/size;
            Pageable pageable = PageRequest.of(dbPage, size - cachedMessages.size());
            dbMessages = messageRepository.findAllByChatRoomOrderBySendTimeDesc(chatRoom, pageable).getContent();
        }
        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(cachedMessages);
        allMessages.addAll(dbMessages);
        allMessages.sort(Comparator.comparing(ChatMessage::getSendTime));

        int totalElements = allMessages.size();
        int totalPage = (int) (double) (totalElements / size);
        int startIndex = (page -1) * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<ChatMessage> pageMessages = allMessages.subList(startIndex, endIndex);
        return new PageImpl<>(pageMessages, PageRequest.of(page, size), totalPage);
    }


    /*유저 정보 검증 메서드*/
    private User findAuthenticatedUser(UserDetailsImpl userDetails) {
        return userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }
}
