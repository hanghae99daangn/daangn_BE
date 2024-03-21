package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SignupRequestDto {
    @Email(message = "이메일 형식에 맞춰 작성해주세요!")
    @Schema(description = "이메일", example = "test123@test.com")
    private String email;

//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
//            message = "비밀번호는 최소 8자에서 최대 15자까지, 소문자, 대문자, 숫자, 특수 문자(@, $, !, %, *, ?, &)를 포함해야 합니다.")
    @Schema(description = "비밀번호", example = "!234Qwer")
    private String password;

    @NotBlank(message = "올바른 전화번호를 입력해주세요!")
    @Schema(description = "전화번호", example = "010 1234 5678")
    private String phoneNumber;

    @NotBlank(message = "올바른 닉네임을 입력해주세요!")
    @Schema(description = "닉네임", example = "밤양갱")
    private String nickname;

    @NotBlank(message = "올바른 주소를 입력해주세요!")
    @Schema(description = "주소", example = "서울시 구로구 오류동")
    private String address;

    public User toEntity(String password, UserRoleEnum role) {
        return User.builder()
                .email(this.email)
                .password(password)
                .phoneNumber(this.phoneNumber.replace(" ", ""))
                .nickname(this.nickname)
                .address(this.address)
                .role(role)
                .build();
    }
}
