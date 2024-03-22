package com.sparta.market.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    private String commentContent;
    private Long parentCommentId;

    @Builder
    public CommentRequestDto(String commentContent, Long parentCommentId) {
        this.commentContent = commentContent;
        this.parentCommentId = parentCommentId;
    }
}
