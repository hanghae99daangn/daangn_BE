package com.sparta.market.domain.comment.repository;

import com.sparta.market.domain.comment.entity.Comment;
import com.sparta.market.domain.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentCustomRepository {
    Optional<Comment> findByCommunityAndCommentId(Community community, Long commentId);

    Page<Comment> findByCommunityAndParentCommentIsNull(Community community, Pageable pageable);
}
