package com.sparta.market.domain.user.service;

import com.sparta.market.domain.user.dto.*;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserProfile;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import com.sparta.market.domain.user.repository.UserProfileRepository;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.aws.service.S3UploadService;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
import com.sparta.market.global.redis.RedisUtil;
import com.sparta.market.global.security.jwt.JwtUtil;
import com.sparta.market.global.sms.EmailHtmlString;
import com.sparta.market.global.sms.MailService;
import com.sparta.market.global.sms.SmsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;
    private final UserProfileRepository userProfileRepository;
    private final MailService mailService;
    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;
    private final EmailHtmlString emailHtmlString;
    private final JwtUtil jwtUtil;

    public SignupResponseDto signUp(SignupRequestDto requestDto, MultipartFile multipartFile) throws IOException {

        checkDuplicatedEmail(requestDto.getEmail());
        checkDuplicatedPhone(requestDto.getPhoneNumber());

        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = requestDto.toEntity(password, UserRoleEnum.USER);
        User savedUser = userRepository.save(user);

        if (multipartFile != null) {
            String filename = multipartFile.getOriginalFilename();
            String imageUrl = s3UploadService.saveFile(multipartFile, multipartFile.getOriginalFilename());
            UserProfile userProfile = UserProfile.builder()
                    .url(imageUrl)
                    .imageName(multipartFile.getOriginalFilename())
                    .s3name(filename)
                    .user(user)
                    .build();
            UserProfile savedUserProfile = userProfileRepository.save(userProfile);
            return new SignupResponseDto(savedUser, savedUserProfile);
        }

        return new SignupResponseDto(savedUser);
    }

    public String sendCodeToPhone(PhoneDto checkDto) {
        checkDuplicatedPhone(checkDto.getPhoneNumber());

        String phoneNumber = checkDto.getPhoneNumber().replaceAll(" ","");
        String verificationCode = smsUtil.createCode();
        redisUtil.setDataExpire(verificationCode, phoneNumber, 60*5L);

        return verificationCode;
    }

    public String sendCodeToPhoneLogin(PhoneDto checkDto) {
        String phoneNumber = checkDto.getPhoneNumber().replaceAll(" ","");
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_USER)
        );

        String verificationCode = smsUtil.createCode();
        redisUtil.setDataExpire(verificationCode, phoneNumber, 60*5L);

        return verificationCode;
    }

    public boolean checkPhoneCode(PhoneCheckDto phoneCheckDto) {
        String phoneNumber = phoneCheckDto.getPhoneNumber().replaceAll(" ","");
        try {
            String result = redisUtil.getData(phoneCheckDto.getVerificationCode());
            return result.equals(phoneNumber);
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.MSG_TIME_OUT);
        }
    }

    public String sendCodeToEmail(String email) {
        checkDuplicatedEmail(email);

        String verificationCode = smsUtil.createCode();
        redisUtil.setDataExpire(verificationCode, email, 60 * 5L);

        String title = "[바니마켓] 이메일 인증번호 요청";

        String content = emailHtmlString.generateEmailHtmlContent(verificationCode);
        mailService.sendEmail(email, title, content);

        return verificationCode;
    }

    public boolean checkEmailCode(EmailCheckDto emailCheckDto) {
        String email = emailCheckDto.getEmail();
        try {
            String result = redisUtil.getData(emailCheckDto.getVerificationCode());
            return result.equals(email);
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.MSG_TIME_OUT);
        }
    }

    public PhoneLoginResponseDto loginByPhone(PhoneLoginRequestDto requestDto) {
        String phoneNumber = requestDto.getPhoneNumber().replaceAll(" ","");
        try {
            String result = redisUtil.getData(requestDto.getVerificationCode());
            String token = jwtUtil.createToken(phoneNumber, UserRoleEnum.USER);

            if (result.equals(phoneNumber)) {
                User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->
                        new CustomException(ErrorCode.NOT_EXIST_USER)
                );

                if (userProfileRepository.findByUserId(user.getId()).isPresent()){
                    UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElseThrow(()->
                            new CustomException(ErrorCode.NOT_EXIST_IMG));
                    return new PhoneLoginResponseDto(user, profile, token);
                }

                return new PhoneLoginResponseDto(user, token);
            }
        } catch (NullPointerException e) {
            throw new CustomException(ErrorCode.MSG_TIME_OUT);
        }
        return null;
    }

    /* 검증 및 로직 메서드 */

    private void checkDuplicatedEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void checkDuplicatedPhone(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        }
    }
}
