package com.sparta.market.domain.trade.dto;


import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TradeRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class CreateTradeRequestDto{
        @Schema(description = "제목", example = "아이패드 팔아요~")
        private String title;
        @Schema(description = "내용", example = "5년 쓴 아이패드 팝니다! 충전기 꽂아두면 사용할 수 있어요!")
        private String content;
        @Schema(description = "카테고리", example = "전자기기")
        private String category;
        @Schema(description = "가격", example = "150000")
        private int price;
        @Schema(description = "만날 장소", example = "서울시 구로구 오류동")
        private String contactPlace;
        public TradePost toEntity(User user) {
            return TradePost.builder()
                    .title(this.title)
                    .content(this.content)
                    .category(this.category)
                    .price(this.price)
                    .contactPlace(this.contactPlace)
                    .user(user)
                    .build();
        }
    }

}
