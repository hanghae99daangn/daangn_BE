package com.sparta.market.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor // Lombok에서 가져옴. 파라미터가 없는 기본 생성자를 만들어준다.
@Table(name = "users")
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column (nullable = false)
    private String nickname;

    @Column (nullable = false, unique = true)
    private String phoneNumber;

    @Column (nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    /*커뮤니티 게시글 테스트용 Builder 코드*/
    @Builder
    public User (String email) {
        this.email = email;
    }
    @Builder
    public User (Long id) {
        this.id = id;
    }

    @Builder
    public User(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
