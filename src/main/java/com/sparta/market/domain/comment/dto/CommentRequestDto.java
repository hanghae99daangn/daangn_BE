package com.sparta.market.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    private String commentContent;

    @Builder
    public CommentRequestDto(String commentContent) {
        this.commentContent = commentContent;
    }
}
