package com.sparta.market.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunityRequestDto {

    private String title;
    private String content;

    /*단위 테스트를 위해 추가*/
    @Builder
    public CommunityRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
