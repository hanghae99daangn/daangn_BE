package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetAllCommunityResponseDto {

    private Long communityId;
    private String title;
    private String nickname;
    private String contents;
    private CommunityCategory category;
    private LocalDateTime createdAt;
    private DetailCommunityImageResponseDto communityImage;

    @Builder
    public GetAllCommunityResponseDto(Community community) {
        this.communityId = community.getCommunityId();
        this.title = community.getTitle();
        this.nickname = community.getUser().getNickname();
        this.contents = community.getContent();
        this.category = community.getCategory();
        this.createdAt = community.getCreatedAt();
        if (!community.getCommunityImages().isEmpty()) {
            this.communityImage = community.getCommunityImages()
                    .stream()
                    .findFirst()
                    .map(DetailCommunityImageResponseDto::new)
                    .orElse(null);
        }
    }
}
