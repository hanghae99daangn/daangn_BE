package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.PhoneDto;
import com.sparta.market.domain.user.dto.PhoneLoginRequestDto;
import com.sparta.market.domain.user.dto.PhoneLoginResponseDto;
import com.sparta.market.domain.user.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 API", description = "로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "전화번호로 로그인 (인증코드 전송)",
            description = "전화번호 입력 -> 인증코드 전송")
    @PostMapping("/login/send-code-phone")
    public ResponseEntity<?> sendCodeToPhoneLogin(@Valid @RequestBody PhoneDto phoneDto) {
        String verificationCode = loginService.sendCodeToPhoneLogin(phoneDto);
        return ResponseEntity.ok().body( "문자를 확인해주세요 (" + verificationCode +")");
    }

    @Operation(summary = "전화번호로 로그인 (인증코드, 전화번호 필요)",
            description = "인증코드와 전화번호 전송시, 검증 후 회원 정보 반환")
    @PostMapping("/login/phone")
    public ResponseEntity<?> loginByPhone(@RequestBody PhoneLoginRequestDto requestDto){
        PhoneLoginResponseDto responseDto = loginService.loginByPhone(requestDto);
        if (responseDto == null) {
            return ResponseEntity.ok().body("로그인 실패");
        }
        return ResponseEntity.ok().body(responseDto);
    }
}
