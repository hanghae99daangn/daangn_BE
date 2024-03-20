package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.LoginRequestDto;
import com.sparta.market.domain.user.dto.SignupRequestDto;
import com.sparta.market.domain.user.dto.SignupResponseDto;
import com.sparta.market.domain.user.service.UserService;
import com.sparta.market.global.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "회원 API", description = "회원가입 및 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> signup(@RequestPart(value = "files", required = false) MultipartFile multipartFile,
                                    @Valid @RequestPart(value = "signupRequestDto") SignupRequestDto requestDto) throws IOException {
        SignupResponseDto responseDto = userService.signUp(requestDto, multipartFile);
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto) {

    }
}
