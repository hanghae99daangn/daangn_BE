package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.LoginRequestDto;
import com.sparta.market.domain.user.dto.SignupRequestDto;
import com.sparta.market.domain.user.dto.SignupResponseDto;
import com.sparta.market.domain.user.service.UserService;
import com.sparta.market.global.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API", description = "회원가입 및 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto){
        SignupResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto){

    }
}
