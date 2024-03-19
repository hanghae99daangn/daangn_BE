package com.sparta.market.domain.trade.repository;

import com.sparta.market.domain.trade.entity.TradePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {
}
