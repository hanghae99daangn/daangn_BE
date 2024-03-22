package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.*;
import com.sparta.market.domain.user.service.UserService;
import com.sparta.market.global.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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

//    @Operation(summary = "전화번호 인증",
//            description = "010 1234 5678 형식으로 보내주세요. 한 건에 20원입니다 ㅠㅠ")
//    @PostMapping("/signup/phone")
//    public ResponseEntity<?> phoneCheck(@RequestBody PhoneCheckDto checkDto){
//        String phoneNumber = checkDto.getPhoneNumber().replaceAll(" ","");
//        String verificationCode = smsUtil.createCode();
//        smsUtil.sendOne(phoneNumber, verificationCode);
//
//        redisUtil.setDataExpire(verificationCode, phoneNumber, 60 * 5L);
//
//        redisUtil.getData(verificationCode);
//
//        return ResponseEntity.ok().body(ResponseDto.success("문자 전송 성공", "문자를 확인해주세요."));
//    }

    @Operation(summary = "회원가입 전화번호 인증",
            description = "010 1234 5678 형식으로 보내주세요. 인증코드 전달")
    @PostMapping("/signup/send-code-phone")
    public ResponseEntity<?> sendCodeToPhone(@Valid @RequestBody PhoneDto phoneDto){
        String verificationCode = userService.sendCodeToPhone(phoneDto);
        return ResponseEntity.ok().body(ResponseDto.success("문자 전송 성공", "문자를 확인해주세요 (" + verificationCode +")"));
    }

    @Operation(summary = "전화 회원가입 인증번호 검증",
            description = "010 1234 5678 형식으로 보내주세요. 전화번호 인증 테스트에서 확인한 번호를 넣어주세요.")
    @PostMapping("/signup/check-code-phone")
    public ResponseEntity<?> checkPhoneCode(@Valid @RequestBody PhoneCheckDto phoneCheckDto){
        boolean result = userService.checkPhoneCode(phoneCheckDto);
        if (result) {
            return ResponseEntity.ok().body(ResponseDto.success("인증번호가 일치합니다.", result));
        } else {
            return ResponseEntity.ok().body(ResponseDto.success("인증번호가 일치하지 않습니다.", result));
        }
    }

    @Operation(summary = "이메일 인증",
            description = "이메일 입력 후, 해당 이메일로 인증 번호 전송")
    @PostMapping("/signup/send-code-email")
    public ResponseEntity<?> sendEmail(@RequestParam("email") @Email String email){
        String verificationCode = userService.sendCodeToEmail(email);
        return ResponseEntity.ok().body(ResponseDto.success("이메일 전송 완료", "인증번호를 확인해주세요! (" + verificationCode + ")"));
    }

    @Operation(summary = "이메일 인증번호 검증",
            description = "이메일 형식으로 보내주세요. 이메일 인증 테스트에서 확인한 번호를 넣어주세요.")
    @PostMapping("/signup/check-code-email")
    public ResponseEntity<?> checkEmailCode(@Valid @RequestBody EmailCheckDto emailCheckDto){
        boolean result = userService.checkEmailCode(emailCheckDto);
        if (result) {
            return ResponseEntity.ok().body(ResponseDto.success("인증번호가 일치합니다.", result));
        } else {
            return ResponseEntity.ok().body(ResponseDto.success("인증번호가 일치하지 않습니다.", result));
        }
    }

    @Operation(summary = "회원가입",
            description = "회원가입시, 프로필 사진 저장 가능")
    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> signup(@RequestPart(value = "files", required = false) MultipartFile multipartFile,
                                    @Valid @RequestPart(value = "signupRequestDto") SignupRequestDto requestDto) throws IOException {
        SignupResponseDto responseDto = userService.signUp(requestDto, multipartFile);
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }

    /* Swagger용 API */
    @Operation(summary = "이메일, 비밀번호 방식 (회의 필요)",
            description = "회원가입 방식 수정 예정")
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto) {

    }

    @Operation(summary = "전화번호로 로그인 (인증코드 전송)",
            description = "전화번호 입력 -> 인증코드 전송")
    @PostMapping("/login/send-code-phone")
    public ResponseEntity<?> sendCodeToPhoneLogin(@Valid @RequestBody PhoneDto phoneDto) {
        String verificationCode = userService.sendCodeToPhoneLogin(phoneDto);
        return ResponseEntity.ok().body(ResponseDto.success("문자 전송 성공", "문자를 확인해주세요 (" + verificationCode +")"));
    }

    @Operation(summary = "전화번호로 로그인 (인증코드, 전화번호 필요)",
            description = "인증코드와 전화번호 전송시, 검증 후 회원 정보 반환")
    @PostMapping("/login/phone")
    public ResponseEntity<?> loginByPhone(@RequestBody PhoneLoginRequestDto requestDto){
        PhoneLoginResponseDto responseDto = userService.loginByPhone(requestDto);
        if (responseDto == null) {
            return ResponseEntity.ok().body(ResponseDto.success("로그인 실패", null));
        }
        return ResponseEntity.ok().body(ResponseDto.success("로그인 성공", responseDto));
    }
}
