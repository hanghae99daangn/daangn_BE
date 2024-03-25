package com.sparta.market.domain.chat.controller;


import com.sparta.market.domain.chat.dto.ChatDto;
import com.sparta.market.domain.chat.dto.MultiResponseDto;
import com.sparta.market.domain.chat.entity.ChatRoom;
import com.sparta.market.domain.chat.mapper.ChatMapper;
import com.sparta.market.domain.chat.response.PageInfo;
import com.sparta.market.domain.chat.service.RoomService;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.security.config.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.sparta.market.global.common.exception.ErrorCode.NOT_EXIST_USER;

@RestController
@Slf4j
@Validated
@RequestMapping("/chats")
@RequiredArgsConstructor
public class RoomController {

    private final UserRepository userRepository;
    private final RoomService roomService;
    private final ChatMapper mapper;

    /* 채팅방 주소 가져오기*/
    @PostMapping
    public ResponseEntity getOrCreateRoom(@Valid @RequestBody ChatDto.Post postDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails == null) {
            log.info("인증되지 않은 회원으로 채팅방을 생성할 수 없음");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User receiver = validateVerifyUser(mapper.ChatPostDtoToMember(postDto).getId());

        long roomId = roomService.createRoom(receiver.getId(), userDetails);

        URI location = UriComponentsBuilder.newInstance()
                .path("/chats/{room-id}")
                .buildAndExpand(roomId)
                .toUri();

        return ResponseEntity.created(location).build();

    }

    /* 채팅방 열기*/
    @GetMapping
    public ResponseEntity getChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @Positive @RequestParam(defaultValue = "1") int page,
                                       @Positive @RequestParam(defaultValue = "10") int size) {

        if(userDetails == null) {
            log.info("인증되지 않은 회원의 접근으로 채팅 목록을 가져올 수 없음");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Page<ChatRoom> roomPage = roomService.findRooms(userDetails, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)roomPage.getTotalElements(), roomPage.getTotalPages());

        List<ChatRoom> rooms = roomPage.getContent();
        List<ChatDto.RoomResponse> responses = mapper.chatRoomListToRoomResponseDtos(rooms);

        return new ResponseEntity<>(new MultiResponseDto<>(responses, pageInfo), HttpStatus.OK);
    }

    /*유저 정보 검증 메서드*/
    private User validateVerifyUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }
}
