package com.sparta.market.domain.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.market.domain.trade.entity.TradePost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TradeResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreatePostResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String content;
        private String category;
        private int price;
        private String contactPlace;
        private int hit;
        private List<String> imageName;
        private List<String> imageUrlList;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public CreatePostResponseDto(TradePost post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.category = post.getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
        }

        public CreatePostResponseDto(TradePost post, List<String> urlList, List<String> nameList) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.category = post.getCategory();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.imageUrlList = urlList;
            this.imageName = nameList;
        }
    }
}
