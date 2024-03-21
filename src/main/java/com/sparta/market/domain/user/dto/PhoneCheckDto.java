package com.sparta.market.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PhoneCheckDto {
    @Schema(description = "전화번호", example = "010 1234 5678")
    @Pattern(regexp = "^010\\s\\d{4}\\s\\d{4}$", message = "올바른 전화번호 형식을 보내주세요.")
    private String phoneNumber;

    @Schema(description = "인증번호", example = "123456")
    private String checkNumber;
}
