package com.sparta.market.domain.community.repository;

import com.sparta.market.domain.community.entity.CommunityImage;

import java.util.List;

public interface CommunityImageCustomRepository {
    List<CommunityImage> findAllByCommunityCommunityId(Long communityId);
}
