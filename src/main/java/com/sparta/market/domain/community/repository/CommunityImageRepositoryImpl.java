package com.sparta.market.domain.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.market.domain.community.entity.CommunityImage;
import com.sparta.market.domain.community.entity.QCommunityImage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommunityImageRepositoryImpl implements CommunityImageCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommunityImage> findAllByCommunityCommunityId(Long communityId) {
        QCommunityImage communityImage = QCommunityImage.communityImage;

        return queryFactory
                .selectFrom(communityImage)
                .where(communityImage.community.communityId.eq(communityId))
                .fetch();
    }
}
