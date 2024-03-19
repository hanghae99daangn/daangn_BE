package com.sparta.market.domain.community.entity;

import com.sparta.market.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Community Entity", description = "커뮤니티 게시글 엔티티 클래스")
@Entity
@Table(name = "community")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @Column
    @Schema(name = "community post title", description = "커뮤니티 게시글 제목", example = "오늘 저녁 뭐 먹지?")
    private String title;

    @Column
    @Schema(name = "community post contents", description = "커뮤니티 게시글 내용", example = "저녁 메뉴 추천해주세요!")
    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Schema(name = "community post userId", description = "커뮤니티 게시글 생성 유저 ID")
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    @Schema(name = "community post image id", description = "커뮤니티 게시글 이미지 ID 리스트")
    private List<CommunityImage> communityImages;

    @CreatedDate
    @Schema(name = "community post created time", description = "커뮤니티 게시글 작성 시간")
    private LocalDateTime createdAt;

    @Builder
    public Community(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }
}
