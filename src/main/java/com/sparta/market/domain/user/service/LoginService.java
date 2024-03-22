package com.sparta.market.domain.user.service;

import com.sparta.market.domain.user.dto.PhoneDto;
import com.sparta.market.domain.user.dto.PhoneLoginRequestDto;
import com.sparta.market.domain.user.dto.PhoneLoginResponseDto;
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

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;
    private final UserProfileRepository userProfileRepository;
    private final MailService mailService;
    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;
    private final EmailHtmlString emailHtmlString;
    private final JwtUtil jwtUtil;

    public String sendCodeToPhoneLogin(PhoneDto checkDto) {
        String phoneNumber = checkDto.getPhoneNumber().replaceAll(" ","");
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_USER)
        );

        String verificationCode = smsUtil.createCode();
        redisUtil.setDataExpire(verificationCode, phoneNumber, 60*5L);

        return verificationCode;
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

}
