package com.sparta.market.domain.trade.repository;

import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {

    Optional<TradePost> findByIdAndUser(Long postId, User user);

    Page<TradePost> findAllByCategory(String category, Pageable page);

    Page<TradePost> findAllByContactPlaceContaining(String dong, Pageable page);

}
