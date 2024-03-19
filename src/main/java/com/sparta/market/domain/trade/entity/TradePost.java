package com.sparta.market.domain.trade.entity;

import com.sparta.market.domain.user.entity.User;
import com.sparta.market.global.common.entity.Timestamped;
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
@Table(name = "trade_posts")
public class TradePost extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column (nullable = false)
    private String category;

    @Column (columnDefinition = "integer default 0", nullable = false)
    private int price;

    @Column (nullable = false)
    private String content;

    @Column (columnDefinition = "integer default 0", nullable = false)
    private int hit;

    private String contactPlace;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
