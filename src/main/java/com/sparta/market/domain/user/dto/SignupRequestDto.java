package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SignupRequestDto {
    private String email;
    private String password;
    private String phoneNumber;
    private String nickname;

    public User toEntity(String password) {
        return User.builder()
                .email(this.email)
                .password(password)
                .phoneNumber(this.phoneNumber)
                .nickname(this.nickname)
                .build();
    }
}
