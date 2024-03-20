package com.sparta.market.domain.trade.dto;


import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
        @NotBlank(message = "제목을 입력하세요!")
        private String title;

        @Schema(description = "내용", example = "5년 쓴 아이패드 팝니다! 충전기 꽂아두면 사용할 수 있어요!")
        @NotBlank(message = "내용을 입력하세요!")
        private String content;

        @Schema(description = "카테고리", example = "전자기기")
        @NotBlank(message = "카테고리를 입력하세요!")
        private String category;

        @Schema(description = "가격", example = "150000")
        @Min(value = 10, message = "10원 이상의 금액을 입력해주세요!")
        private int price;

        @Schema(description = "만날 장소", example = "서울시 구로구 오류동")
        @NotBlank(message = "만날 장소를 입력하세요!")
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

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class UpdateTradeRequestDto{
        @Schema(description = "제목", example = "아이패드 팔아요! 가격 인하")
        @NotBlank(message = "제목을 입력하세요!")
        private String title;

        @Schema(description = "내용", example = "몇 년 안 쓴 아이패드 팔아요!")
        @NotBlank(message = "내용을 입력하세요!")
        private String content;

        @Schema(description = "카테고리", example = "전자기기")
        @NotBlank(message = "카테고리를 입력하세요!")
        private String category;

        @Schema(description = "가격", example = "130000")
        @Min(value = 10, message = "10원 이상의 금액을 입력해주세요!")
        private int price;

        @Schema(description = "만날 장소", example = "서울시 구로구 개봉동")
        @NotBlank(message = "만날 장소를 입력하세요!")
        private String contactPlace;

        @Schema(description = "해당 게시글에 이미지가 있었던 경우, 그 imgId를 입력해주세요", example = "1")
        private Long imgId;

    }
}
