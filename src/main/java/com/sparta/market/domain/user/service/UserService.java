package com.sparta.market.domain.user.service;

import com.sparta.market.domain.user.dto.SignupRequestDto;
import com.sparta.market.domain.user.dto.SignupResponseDto;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signUp(SignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        if (userRepository.findByPhoneNumber(requestDto.getPhoneNumber()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        }
        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = requestDto.toEntity(password, UserRoleEnum.USER);
        User savedUser = userRepository.save(user);

        return new SignupResponseDto(savedUser);
    }

}
