package com.sparta.market.domain.community.entity;

import lombok.Getter;

@Getter
public enum CommunityCategory {
    동네질문("동네질문"),
    생활정보("생활정보"),
    동네맛집("동네맛집")
    ;

    private final String category;
    CommunityCategory(String category) {
        this.category = category;
    }
}
