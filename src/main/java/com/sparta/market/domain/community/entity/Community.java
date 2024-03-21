package com.sparta.market.domain.community.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(length = 2000)
    @Schema(name = "community post contents", description = "커뮤니티 게시글 내용", example = "저녁 메뉴 추천해주세요!")
    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Schema(name = "community post userId", description = "커뮤니티 게시글 생성 유저 ID")
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @Schema(name = "community post image id", description = "커뮤니티 게시글 이미지 ID 리스트")
    private List<CommunityImage> communityImages;

    @CreatedDate
    @Schema(name = "community post created time", description = "커뮤니티 게시글 작성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Schema(name = "community post category", description = "커뮤니티 게시글 카테고리")
    private CommunityCategory category;

    @Column
    @Schema(name = "community post user address", description = "커뮤니티 게시글 생성 유저 주소 정보")
    private String address;

    @Builder
    public Community(String title, String content, User user, CommunityCategory category, String address) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.category = category;
        this.address = address;
    }

    public void updatePost(String title, String content, CommunityCategory category, String address) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.address = address;
    }

    /* 테스트 코드용 Builder 코드*/
    @Builder
    public Community(Long communityId, String title, String content, User user, CommunityCategory category, String address) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.user = user;
        this.category = category;
        this.address = address;
    }
}
