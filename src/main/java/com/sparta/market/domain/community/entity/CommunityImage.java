package com.sparta.market.domain.community.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;

@Tag(name = "community image entity", description = "커뮤니티 게시글 이미지 엔티티 클래스")
@Entity
@Table(name = "communityImage")
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column
    @Schema(name = "image name", description = "이미지 고유 이름")
    private String imageName;

    @Column
    @Schema(name = "s3 image name")
    private String s3name;

    @Column
    @Schema(name = "image url", description = "이미지 url 주소")
    private String url;
}
