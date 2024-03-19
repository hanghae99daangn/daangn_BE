package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.Community;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityResponseDto {

    private Long communityId;
    private String title;
    private String nickName;
    private String contents;
    private LocalDateTime createdAt;

    public CommunityResponseDto(Community community) {
        this.communityId = community.getCommunityId();
        this.title = community.getTitle();
        this.nickName = community.getUser().getNickname();
        this.contents = community.getContent();
        this.createdAt = community.getCreatedAt();
    }
}
