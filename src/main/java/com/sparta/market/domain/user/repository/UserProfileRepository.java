package com.sparta.market.domain.user.repository;

import com.sparta.market.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
