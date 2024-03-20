package com.sparta.market.domain.trade.repository;

import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {

    Optional<TradePost> findByIdAndUser(Long postId, User user);

    List<TradePost> findAllByCategory(String category);
}
