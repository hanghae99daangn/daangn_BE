package com.sparta.market.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.market.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {

    private Long commentId;
    private String commentContent;
    private String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<CommentResponseDto> childComments; /* 대댓글 목록*/


    @Builder
    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.commentContent = comment.getCommentContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
    }

    /* 대댓글 조회용*/
    @Builder
    public CommentResponseDto(Comment comment, List<CommentResponseDto> childComments) {
        this.commentId = comment.getCommentId();
        this.commentContent = comment.getCommentContent();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.childComments = childComments; /* 대댓글 목록 할당*/
    }
}
