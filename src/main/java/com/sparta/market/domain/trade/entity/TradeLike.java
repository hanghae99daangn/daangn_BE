package com.sparta.market.domain.trade.entity;

import com.sparta.market.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "trade_likes")
public class TradeLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LikeStateEnum likeState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id")
    private TradePost tradePost;

    @Builder
    public TradeLike(LikeStateEnum likeState, TradePost tradePost, User user) {
        this.likeState = likeState;
        this.tradePost = tradePost;
        this.user = user;
    }

    public void update() {
        if(this.likeState == LikeStateEnum.ENABLED){
            this.likeState = LikeStateEnum.DISABLED;
        } else {
            this.likeState = LikeStateEnum.ENABLED;
        }
    }
}
