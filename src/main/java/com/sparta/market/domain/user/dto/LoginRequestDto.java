package com.sparta.market.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @Email(message = "이메일 형식에 맞춰 작성해주세요!")
    @Schema(description = "이메일", example = "test123@test.com")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 최소 8자에서 최대 15자까지, 소문자, 대문자, 숫자, 특수 문자(@, $, !, %, *, ?, &)를 포함해야 합니다.")
    @Schema(description = "비밀번호", example = "!234Qwer")
    private String password;
}
