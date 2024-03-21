package com.sparta.market.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.market.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long commentId;
    private String commentContent;
    private String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Builder
    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.commentContent = comment.getCommentContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
    }
}
