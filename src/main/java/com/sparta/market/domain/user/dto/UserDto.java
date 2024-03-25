package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    @Builder
    public static class ResponseOnlyUserName {
        private long userId;
        private String nickName;
        private UserProfile userProfile;
    }
}
