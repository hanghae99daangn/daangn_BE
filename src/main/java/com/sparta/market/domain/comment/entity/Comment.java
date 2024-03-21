package com.sparta.market.domain.comment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 100)
    @Schema(name = "Comment Content", description = "댓글 내용", example = "와 너무 유익해요!")
    @NotNull(message = "빈 댓글은 쓸 수 없습니다.")
    private String commentContent;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "community_id")
    @Schema(name = "커뮤니티 게시글 ID")
    private Community community;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @Schema(name = "유저 ID")
    private User user;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(name = "댓글 생성 시간")
    private LocalDateTime createdAt;

    @Builder
    public Comment(String commentContent, Community community, User user) {
        this.commentContent = commentContent;
        this.community = community;
        this.user = user;
    }

    public void updateComment(String commentContent) {
        this.commentContent = commentContent;
    }
}
