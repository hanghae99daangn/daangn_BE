package com.sparta.market.domain.user.service;

import com.sparta.market.domain.user.dto.SignupRequestDto;
import com.sparta.market.domain.user.dto.SignupResponseDto;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserProfile;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import com.sparta.market.domain.user.repository.UserProfileRepository;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.aws.service.S3UploadService;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
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

    public SignupResponseDto signUp(SignupRequestDto requestDto, MultipartFile multipartFile) throws IOException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        if (userRepository.findByPhoneNumber(requestDto.getPhoneNumber()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_PHONE_NUMBER);
        }

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

}
