package com.sparta.market.domain.trade.entity;

import com.sparta.market.domain.trade.dto.TradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeRequestDto.UpdateTradeRequestDto;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private String contactPlace;

    @Column (columnDefinition = "integer default 0", nullable = false)
    private int hit;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "tradePost", cascade = CascadeType.ALL)
    private List<TradePostImage> postImageList;

    public void update(UpdateTradeRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.price = requestDto.getPrice();
        this.category = requestDto.getCategory();
        this.contactPlace = requestDto.getContactPlace();
    }
    public void updateHit(){
        this.hit++;
    }
}
