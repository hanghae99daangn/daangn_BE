package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupResponseDto {
    private Long id;
    private String nickname;
    private String phoneNumber;
    private String email;
    private Long profileId;
    private String imageName;
    private String s3Name;
    private String url;

    public SignupResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
    }
    public SignupResponseDto(User user, UserProfile userProfile) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.profileId = userProfile.getId();
        this.imageName = userProfile.getImageName();
        this.s3Name = userProfile.getS3name();
        this.url = userProfile.getUrl();
    }
}
