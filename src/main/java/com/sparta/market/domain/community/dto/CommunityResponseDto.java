package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommunityResponseDto {

    private Long communityId;
    private String title;
    private String nickname;
    private String contents;
    private CommunityCategory category;
    private LocalDateTime createdAt;
    private String address;
    private List<String> imageNameList;
    private List<String> imageUrlList;

    @Builder
    public CommunityResponseDto(Community community) {
        this.communityId = community.getCommunityId();
        this.title = community.getTitle();
        this.nickname = community.getUser().getNickname();
        this.contents = community.getContent();
        this.category = community.getCategory();
        this.createdAt = community.getCreatedAt();
        this.address = community.getAddress();
    }

    @Builder
    public CommunityResponseDto(Community community, List<String> urlList, List<String> nameList) {
        this.communityId = community.getCommunityId();
        this.title = community.getTitle();
        this.nickname = community.getUser().getNickname();
        this.contents = community.getContent();
        this.category = community.getCategory();
        this.createdAt = community.getCreatedAt();
        this.address = community.getAddress();
        this.imageNameList = nameList;
        this.imageUrlList = urlList;
    }

}
