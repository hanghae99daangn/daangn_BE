package com.sparta.market.domain.chat.controller;


import com.sparta.market.domain.chat.dto.ChatDto;
import com.sparta.market.domain.chat.dto.MessageDto;
import com.sparta.market.domain.chat.dto.MultiResponseDto;
import com.sparta.market.domain.chat.entity.ChatMessage;
import com.sparta.market.domain.chat.entity.PublishMessage;
import com.sparta.market.domain.chat.mapper.ChatMapper;
import com.sparta.market.domain.chat.response.PageInfo;
import com.sparta.market.domain.chat.service.ChatService;
import com.sparta.market.global.security.config.UserDetailsImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;
    private final ChatMapper mapper;

    private final ChannelTopic topic;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate redisTemplate;

    @MessageMapping("/chats/messages/{room-id}")
    public void message(@DestinationVariable("room-id") Long roomId, MessageDto messageDto,
                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PublishMessage publishMessage =
                new PublishMessage(messageDto.getRoomId(), messageDto.getSenderId(), messageDto.getContent(), LocalDateTime.now());

        /* 채팅방에 메세지 전송 */
        redisTemplate.convertAndSend(topic.getTopic(), publishMessage);

        chatService.CachedMessage(messageDto, roomId, userDetails);
    }

    /* 채팅메세지 가져오기*/
    @GetMapping("/chats/messages/{room-id}")
    public ResponseEntity<?> getMessages(@Positive @PathVariable("room-id") long roomId,
                                      @Positive @RequestParam(defaultValue = "1") int page,
                                      @Positive @RequestParam(defaultValue = "10") int size,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if(userDetails == null) {
            log.error("인증되지 않은 회원의 접근으로 메세지를 가져올 수 없음");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 해당 채팅방의 메세지를 가져와야 함
        Page<ChatMessage> messages = chatService.findMessages(roomId, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)messages.getTotalElements(), messages.getTotalPages());

        List<ChatMessage> messageList = messages.getContent();
        List<ChatDto.MessageResponse> messageResponses = mapper.messagesToMessageResponseDtos(messageList);

        return new ResponseEntity<>(new MultiResponseDto<>(messageResponses, pageInfo), HttpStatus.OK);
    }
}
