package com.sparta.market.community.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import lombok.Getter;

@Tag(name = "Community Entity", description = "커뮤니티 게시글 엔티티 클래스")
@Entity
@Table(name = "community")
@Getter
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CommunityId;

}
