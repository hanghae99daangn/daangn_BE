package com.sparta.market.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class EmailCheckDto {
    @Schema(description = "전화번호", example = "010 1234 5678")
    @Email(message = "이메일 형식에 맞춰 작성해주세요!")
    private String email;

    @Schema(description = "인증번호", example = "123456")
    private String verificationCode;
}
