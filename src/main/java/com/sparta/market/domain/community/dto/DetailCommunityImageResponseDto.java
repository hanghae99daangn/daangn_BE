package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.CommunityImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetailCommunityImageResponseDto {

    private Long imageId;
    private String imageName;
    private String url;

    @Builder
    public DetailCommunityImageResponseDto(CommunityImage communityImage) {
        this.imageId = communityImage.getImageId();
        this.imageName = communityImage.getImageName();
        this.url = communityImage.getUrl();
    }
}
