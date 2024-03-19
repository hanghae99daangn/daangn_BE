package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.SignupRequestDto;
import com.sparta.market.domain.user.dto.SignupResponseDto;
import com.sparta.market.domain.user.service.UserService;
import com.sparta.market.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto requestDto){
        SignupResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

}
