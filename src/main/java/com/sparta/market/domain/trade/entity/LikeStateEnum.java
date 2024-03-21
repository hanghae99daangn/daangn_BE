package com.sparta.market.domain.trade.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeStateEnum {
    ENABLED("활성화"),
    DISABLED("비활성화");

    private final String state;
}
