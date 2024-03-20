package com.sparta.market.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @Schema(description = "이메일", example = "test123@test.com")
    private String email;
    @Schema(description = "비밀번호", example = "!234Qwer")
    private String password;
}
