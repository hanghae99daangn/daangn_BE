package com.sparta.market.domain.trade.entity;

import lombok.Getter;

@Getter
public enum TradeStateEnum {
    판매("판매"),
    예약("예약"),
    완료("완료");

    private final String state;

    TradeStateEnum(String state) {
        this.state = state;
    }
}
