package com.sparta.market.domain.community.dto;

import com.sparta.market.domain.community.entity.CommunityCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunityRequestDto {

    private String title;
    private String content;
    private CommunityCategory category;
    private String address;

    /*단위 테스트를 위해 추가*/
    @Builder
    public CommunityRequestDto(String title, String content, CommunityCategory category, String address) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.address = address;
    }
}
