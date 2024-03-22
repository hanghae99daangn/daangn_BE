package com.sparta.market.domain.comment.entity;

import com.fasterxml.jackson.annotation.*;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @Schema(name = "부모 댓글 여부 확인")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    @Schema(name = "대댓글 목록")
    private List<Comment> childComments = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    @Schema(name = "부모 댓글 삭제 상태")
    private boolean isDeleted = false;

    @Builder
    public Comment(String commentContent, Community community, User user, Comment parentComment) {
        this.commentContent = commentContent;
        this.community = community;
        this.user = user;
        this.parentComment = parentComment;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updateComment("삭제된 댓글입니다");
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void updateComment(String commentContent) {
        this.commentContent = commentContent;
    }
}
