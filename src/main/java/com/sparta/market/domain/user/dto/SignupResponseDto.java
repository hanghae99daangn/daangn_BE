package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupResponseDto {
    private Long id;
    private String nickname;
    private String phoneNumber;
    private String email;

    public SignupResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
    }
}
