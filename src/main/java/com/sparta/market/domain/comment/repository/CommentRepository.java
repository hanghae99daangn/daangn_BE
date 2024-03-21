package com.sparta.market.domain.comment.repository;

import com.sparta.market.domain.comment.entity.Comment;
import com.sparta.market.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommunityAndCommentId(Community community, Long commentId);
}
