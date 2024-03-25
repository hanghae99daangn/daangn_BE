package com.sparta.market.domain.user.controller;

import com.sparta.market.domain.user.dto.*;
import com.sparta.market.domain.user.service.UserService;
import com.sparta.market.global.common.dto.ResponseDto;
import com.sparta.market.global.security.config.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "회원 API", description = "회원가입, 회원정보수정")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "회원가입 전화번호 인증 (실제로 보내지는 API)",
            description = "010 1234 5678 형식으로 보내주세요. 실제로 전송이 되므로, 사용시 주의")
    @PostMapping("/signup/phone")
    public ResponseEntity<?> phoneCheck(@RequestBody PhoneDto checkDto){
        userService.sendSms(checkDto);
        return ResponseEntity.ok().body(ResponseDto.success("문자 전송 성공", "문자를 확인해주세요."));
    }

    @Operation(summary = "회원가입 전화번호 인증 (verificationCode 보여주는 API)",
            description = "010 1234 5678 형식으로 보내주세요. 인증코드 전달")
    @PostMapping("/signup/send-code-phone")
    public ResponseEntity<?> sendCodeToPhone(@Valid @RequestBody PhoneDto phoneDto){
        String verificationCode = userService.sendCodeToPhone(phoneDto);
        return ResponseEntity.ok().body("문자를 확인해주세요 (" + verificationCode +")");
    }

    @Operation(summary = "전화 회원가입 인증번호 검증",
            description = "010 1234 5678 형식으로 보내주세요. 전화번호 인증 테스트에서 확인한 번호를 넣어주세요.")
    @PostMapping("/signup/check-code-phone")
    public ResponseEntity<?> checkPhoneCode(@Valid @RequestBody PhoneCheckDto phoneCheckDto){
        boolean result = userService.checkPhoneCode(phoneCheckDto);
        if (result) {
            return ResponseEntity.ok().body("인증번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }
    }

    @Operation(summary = "이메일 회원가입 인증",
            description = "이메일 입력 후, 해당 이메일로 인증 번호 전송")
    @PostMapping("/signup/send-code-email")
    public ResponseEntity<?> sendEmail(@RequestParam("email") @Email String email){
        String verificationCode = userService.sendCodeToEmail(email);
        return ResponseEntity.ok().body( "인증번호를 확인해주세요! (" + verificationCode + ")");
    }

    @Operation(summary = "이메일 회원가입 인증번호 검증",
            description = "이메일 형식으로 보내주세요. 이메일 인증 테스트에서 확인한 번호를 넣어주세요.")
    @PostMapping("/signup/check-code-email")
    public ResponseEntity<?> checkEmailCode(@Valid @RequestBody EmailCheckDto emailCheckDto){
        boolean result = userService.checkEmailCode(emailCheckDto);
        if (result) {
            return ResponseEntity.ok().body("인증번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }
    }

    @Operation(summary = "회원가입",
            description = "회원가입시, 프로필 사진 저장 가능")
    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> signup(@RequestPart(value = "files", required = false) MultipartFile multipartFile,
                                    @Valid @RequestPart(value = "signupRequestDto") SignupRequestDto requestDto) throws IOException {
        UserResponseDto responseDto = userService.signUp(requestDto, multipartFile);
        return ResponseEntity.ok().body(responseDto);
    }

    // 작업중
    @Operation(summary = "회원정보 수정")
    @PostMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> userUpdate(@RequestPart(value = "files", required = false) MultipartFile multipartFile,
                                        @Valid @RequestPart(value = "userRequestDto", required = false) UserRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        UserResponseDto responseDto = userService.updateUser(userDetails.getUser(), requestDto, multipartFile);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "유저 정보 보내는 API")
    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        UserResponseDto responseDto = userService.userInfo(userDetails.getUser());
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "프로필 사진 삭제",
            description = "삭제할 경우, response - null 4개")
    @DeleteMapping("/delete-profile/{profileId}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long profileId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        UserResponseDto responseDto = userService.deleteProfile(profileId, userDetails.getUser());
        return ResponseEntity.ok().body(responseDto);
    }
}