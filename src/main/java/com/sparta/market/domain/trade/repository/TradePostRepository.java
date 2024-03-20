package com.sparta.market.domain.trade.repository;

import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {

    Optional<TradePost> findByIdAndUser(Long postId, User user);

}
