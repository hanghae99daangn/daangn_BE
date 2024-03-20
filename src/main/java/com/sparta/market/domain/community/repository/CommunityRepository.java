package com.sparta.market.domain.community.repository;

import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Optional<Community> findByCommunityId(Long communityId);

    Page<Community> findByCategory(CommunityCategory category, Pageable pageable);
}
