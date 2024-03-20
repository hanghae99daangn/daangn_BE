package com.sparta.market.domain.community.entity;

import lombok.Getter;

@Getter
public enum CommunityCategory {
    SAMPLE("SAMPLE"),
    SAMPLE2("SAMPLE2"),
    SAMPLE3("SAMPLE3")
    ;

    private final String category;
    CommunityCategory(String category) {
        this.category = category;
    }
}
