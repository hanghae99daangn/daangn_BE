package com.sparta.market.domain.community.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import com.sparta.market.domain.community.entity.QCommunity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Community> findByCommunityId(Long communityId) {
        QCommunity community = QCommunity.community;
        Community result = queryFactory
                .selectFrom(community)
                .where(community.communityId.eq(communityId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Community> findByCategory(CommunityCategory category, Pageable pageable) {
        QCommunity community = QCommunity.community;

        /* QueryDSL 쿼리 준비*/
        JPAQuery<Community> query = queryFactory.selectFrom(community).where(community.category.eq(category));

        /* 정렬 방향 결정*/
        Sort.Order sortOrder = pageable.getSort().getOrderFor("createdAt");

        OrderSpecifier<?> orderBySpecifier = sortOrder != null
                ? (sortOrder.isAscending() ? community.createdAt.asc() : community.createdAt.desc())
                : community.createdAt.asc();

        query.orderBy(orderBySpecifier);

        /* 페이징 처리를 적용하여 결과 조회 */
        List<Community> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* fetchCount() deprecated 로 인해 사용 불가*/
        Long total = query.select(community.count())
                .from(community).fetchOne();

        /* null 체크를 통해 NullPointerException 방지 */
        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(results, pageable, totalCount);
    }
}
