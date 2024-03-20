package com.sparta.market.domain.community.repository;

import com.sparta.market.domain.community.entity.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    List<CommunityImage> findAllByCommunityCommunityId(Long communityId);
}
