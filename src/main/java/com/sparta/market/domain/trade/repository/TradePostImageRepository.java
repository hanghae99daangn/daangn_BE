package com.sparta.market.domain.trade.repository;

import com.sparta.market.domain.trade.entity.TradePostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradePostImageRepository extends JpaRepository<TradePostImage, Long> {
    List<TradePostImage> findAllByTradePostId(Long tradePostId);
}
