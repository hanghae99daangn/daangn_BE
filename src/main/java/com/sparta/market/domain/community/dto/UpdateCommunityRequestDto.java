package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.CommunityCategory;
import lombok.Getter;

@Getter
public class UpdateCommunityRequestDto {
    private String title;
    private String content;
    private CommunityCategory category;
    private String address;
    private Long imgId;
}
