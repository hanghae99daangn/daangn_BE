package com.sparta.market.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserRequestDto {

    @Schema(description = "유저 Id", example = "1")
    private Long id;

    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "주소", example = "서울시 구로구 오류동")
    private String address;

    @Schema(description = "이메일", example = "test123@test.com")
    private String email;

    @Schema(description = "전화번호", example = "010 1234 5678")
    private String phoneNumber;

    @Schema(description = "프로필 Id", example = "1")
    private Long profileId;

    @Schema(description = "이미지 이름", example = "example.jpg")
    private String imageName;

    @Schema(description = "S3 서버에 저장된 이미지 이름", example = "example.jpg")
    private String s3Name;

    @Schema(description = "이미지 URL", example = "https://market-image.kr.object.ncloudstorage.com/example.jpg")
    private String url;

}
