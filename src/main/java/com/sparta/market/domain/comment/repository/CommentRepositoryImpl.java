package com.sparta.market.domain.comment.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.market.domain.comment.entity.Comment;
import com.sparta.market.domain.comment.entity.QComment;
import com.sparta.market.domain.community.entity.Community;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Comment> findByCommunityAndCommentId(Community community, Long commentId) {
        QComment comment = QComment.comment;
        Comment result = queryFactory
                .selectFrom(comment)
                .where(comment.community.eq(community), comment.commentId.eq(commentId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Comment> findByCommunityAndParentCommentIsNull(Community community, Pageable pageable) {
        QComment comment = QComment.comment;

        /* 부모 댓글이 null인 조건을 추가하여 QueryDSL 쿼리 구성*/
        JPAQuery<Comment> query = queryFactory
                .selectFrom(comment)
                .where(comment.community.eq(community)
                        .and(comment.parentComment.isNull()));

        /* 페이지네이션을 위한 정렬 및 페이징 적용*/
        Sort.Order sortOrder = pageable.getSort().isSorted() ? pageable.getSort().iterator().next() : null;
        OrderSpecifier<?> orderBySpecifier = sortOrder != null
                ? (sortOrder.isAscending() ? comment.createdAt.asc() : comment.createdAt.desc())
                : comment.createdAt.asc();

        query.orderBy(orderBySpecifier);

        /* 페이징 처리를 적용하여 결과 조회 */
        List<Comment> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* 전체 개수 조회를 위한 쿼리
        * fetchCount() deprecated 로 인해 사용 불가*/
        Long total = query.select(comment.count())
                .from(comment)
                .where(comment.community.eq(community)
                        .and(comment.parentComment.isNull()))
                .fetchOne();

        /* null 체크를 통해 NullPointerException 방지 */
        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(results, pageable, totalCount);
    }
}
