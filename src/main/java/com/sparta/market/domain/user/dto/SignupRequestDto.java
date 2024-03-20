package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SignupRequestDto {
    @Schema(description = "이메일", example = "test123@test.com")
    private String email;
    @Schema(description = "비밀번호", example = "!234Qwer")
    private String password;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    @Schema(description = "닉네임", example = "밤양갱")
    private String nickname;
    @Schema(description = "주소", example = "서울시 구로구 오류동")
    private String address;

    public User toEntity(String password, UserRoleEnum role) {
        return User.builder()
                .email(this.email)
                .password(password)
                .phoneNumber(this.phoneNumber)
                .nickname(this.nickname)
                .address(this.address)
                .role(role)
                .build();
    }
}
