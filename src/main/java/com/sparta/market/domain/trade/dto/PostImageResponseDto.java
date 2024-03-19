package com.sparta.market.domain.trade.dto;

import com.sparta.market.domain.trade.entity.TradePostImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostImageResponseDto {

    @NoArgsConstructor
    @Getter
    public static class DetailPostImageResponseDto {
        private Long id;
        private String imageName;
        private String url;

        public DetailPostImageResponseDto(TradePostImage postImage) {
            this.id = postImage.getId();
            this.url = postImage.getUrl();
            this.imageName = postImage.getImageName();
        }
    }
}
