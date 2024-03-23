package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetCommunityResponseDto {

    private Long communityId;
    private String title;
    private String nickname;
    private String content;
    private CommunityCategory category;
    private LocalDateTime createdAt;
    private List<DetailCommunityImageResponseDto> communityImageList = new ArrayList<>();

    @Builder
    public GetCommunityResponseDto(Community community) {
        this.communityId = community.getCommunityId();
        this.title = community.getTitle();
        this.nickname = community.getUser().getNickname();
        this.content = community.getContent();
        this.category = community.getCategory();
        this.createdAt = community.getCreatedAt();
        this.communityImageList = community.getCommunityImages().stream().map(DetailCommunityImageResponseDto::new).toList();
    }

}
