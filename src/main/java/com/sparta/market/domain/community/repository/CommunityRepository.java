package com.sparta.market.domain.community.repository;

import com.sparta.market.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Optional<Community> findByCommunityId(Long communityId);
}
