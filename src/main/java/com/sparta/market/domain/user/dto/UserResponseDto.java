package com.sparta.market.domain.user.dto;

import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDto {
    private Long id;
    private String nickname;
    private String phoneNumber;
    private String email;
    private String address;
    private String dong;
    private Long profileId;
    private String imageName;
    private String s3Name;
    private String url;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.address = user.getAddress();
        String[] parts = this.address.split(" ");
        if (parts.length > 0) {
            this.dong = parts[parts.length - 1];
        } else {
            this.dong = "";
        }
    }
    public UserResponseDto(User user, UserProfile userProfile) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.profileId = userProfile.getId();
        this.imageName = userProfile.getImageName();
        this.s3Name = userProfile.getS3name();
        this.url = userProfile.getUrl();
        String[] parts = this.address.split(" ");
        if (parts.length > 0) {
            this.dong = parts[parts.length - 1];
        } else {
            this.dong = "";
        }
    }
}
