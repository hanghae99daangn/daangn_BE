package com.sparta.market.domain.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.market.domain.trade.dto.PostImageResponseDto.DetailPostImageResponseDto;
import com.sparta.market.domain.trade.entity.TradePost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TradeResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreateTradeResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String content;
        private int price;
        private String category;
        private String contactPlace;
        private int hit;
        private List<String> imageName;
        private List<String> imageUrlList;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public CreateTradeResponseDto(TradePost post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.price = post.getPrice();
            this.category = post.getCategory();
            this.contactPlace = post.getContactPlace();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
        }

        public CreateTradeResponseDto(TradePost post, List<String> urlList, List<String> nameList) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.price = post.getPrice();
            this.category = post.getCategory();
            this.contactPlace = post.getContactPlace();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.imageUrlList = urlList;
            this.imageName = nameList;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateTradeResponseDto {
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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;

        public UpdateTradeResponseDto(TradePost post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.price = post.getPrice();
            this.category = post.getCategory();
            this.contactPlace = post.getContactPlace();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
        }

        public UpdateTradeResponseDto(TradePost post, List<String> urlList, List<String> nameList) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.category = post.getCategory();
            this.price = post.getPrice();
            this.contactPlace = post.getContactPlace();
            this.hit = post.getHit();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            this.imageUrlList = urlList;
            this.imageName = nameList;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class GetPostResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String content;
        private String category;
        private int hit;
        private int price;
        private String contactPlace;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private List<DetailPostImageResponseDto> postImageList = new ArrayList<>();

        public GetPostResponseDto(TradePost post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.category = post.getCategory();
            this.hit = post.getHit();
            this.price = post.getPrice();
            this.contactPlace = post.getContactPlace();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            this.postImageList = post.getPostImageList().stream().map(DetailPostImageResponseDto::new).toList();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class GetPostListResponseDto {
        private Long id;
        private String nickname;
        private String title;
        private String content;
        private String category;
        private int hit;
        private int price;
        private String contactPlace;
        private DetailPostImageResponseDto postImage;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public GetPostListResponseDto(TradePost post) {
            this.id = post.getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.category = post.getCategory();
            this.hit = post.getHit();
            this.price = post.getPrice();
            this.contactPlace = post.getContactPlace();
            this.createdAt = post.getCreatedAt();
            this.modifiedAt = post.getModifiedAt();
            if (!post.getPostImageList().isEmpty()) {
                this.postImage = post.getPostImageList()
                        .stream()
                        .findFirst()
                        .map(DetailPostImageResponseDto::new)
                        .orElse(null);
            }
        }
    }
}
