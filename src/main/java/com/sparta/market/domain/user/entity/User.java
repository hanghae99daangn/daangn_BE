package com.sparta.market.domain.user.entity;

import com.sparta.market.domain.user.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (unique = true)
    private String email;

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

    public void update(UserRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.address = requestDto.getAddress();
        this.email = requestDto.getEmail();
        this.phoneNumber = requestDto.getPhoneNumber();
    }
}
