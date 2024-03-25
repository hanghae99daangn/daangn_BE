package com.sparta.market.domain.chat.dto;

import com.sparta.market.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatDto {
    @Getter
    public static class Post {
        @NotNull
        private long userId; // 채팅을 받는 사람
    }
    @Getter
    @Builder
    public static class RoomResponse {
        private long roomId;
        private UserDto.ResponseOnlyUserName sender;
        private UserDto.ResponseOnlyUserName receiver;
    }
    @Getter
    @Builder
    public static class MessageResponse { // 채팅방 속 하나의 메세지
        private long messageId;
        private UserDto.ResponseOnlyUserName sender;
        private String content;
        private LocalDateTime sendTime;
    }
}
