package com.sparta.market.domain.chat.service;

import com.sparta.market.domain.chat.entity.ChatRoom;
import com.sparta.market.domain.chat.repository.RoomRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.security.config.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sparta.market.global.common.exception.ErrorCode.CHATROOM_NOT_FOUND;
import static com.sparta.market.global.common.exception.ErrorCode.NOT_EXIST_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public Long createRoom(long receiverId, UserDetailsImpl userDetails) {
        User receiver = validateVerifyUser(receiverId);
        User sender = validateVerifyUser(userDetails.getUser().getId());

        /* 둘의 채팅이 있는지 확인*/
        Optional<ChatRoom> optionalChatRoom = roomRepository.findBySenderAndReceiver(sender, receiver);
        Optional<ChatRoom> optionalChatRoom2 = roomRepository.findBySenderAndReceiver(receiver, sender);

        ChatRoom chatRoom = null;

        if(optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
            log.info("find chat room");
            return chatRoom.getRoomId();
        } else if (optionalChatRoom2.isPresent()) {
            chatRoom = optionalChatRoom2.get();
            log.info("find chat room");
            return chatRoom.getRoomId();
        } else {
            chatRoom = ChatRoom.builder().sender(sender).receiver(receiver).build();
            log.info("create chat room");
        }

        ChatRoom saveChatRoom = roomRepository.save(chatRoom);

        return saveChatRoom.getRoomId();
    }

    /* 유저의 채팅 목록 가져오기*/
    public Page<ChatRoom> findRooms(UserDetailsImpl userDetails, int page, int size) {
        User sender = validateVerifyUser(userDetails.getUser().getId());
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("roomId").descending());

        return roomRepository.findAllBySenderOrReceiver(pageable, sender, sender);
    }

    /* 채팅방 하나 찾기*/
    public ChatRoom findRoom(long roomId) {
        ChatRoom chatRoom = findExistRoom(roomId);

        return chatRoom;
    }

    /* 채팅방 존재 검증*/
    private ChatRoom findExistRoom(long roomId) {
        Optional<ChatRoom> optionalChatRoom = roomRepository.findById(roomId);

        return optionalChatRoom.orElseThrow(
                () -> new CustomException(CHATROOM_NOT_FOUND));
    }


    /*유저 정보 검증 메서드*/
    private User validateVerifyUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }
}
