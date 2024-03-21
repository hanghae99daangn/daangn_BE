package com.sparta.market.domain.community.repository;

import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommunityCustomRepository {

    Optional<Community> findByCommunityId(Long communityId);

    Page<Community> findByCategory(CommunityCategory category, Pageable pageable);
}
