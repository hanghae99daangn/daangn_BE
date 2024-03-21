package com.sparta.market.domain.comment.service;

import com.sparta.market.domain.comment.dto.CommentRequestDto;
import com.sparta.market.domain.comment.dto.CommentResponseDto;
import com.sparta.market.domain.comment.entity.Comment;
import com.sparta.market.domain.comment.repository.CommentRepository;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.repository.CommunityRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.market.global.common.exception.ErrorCode.*;

@Tag(name = "커뮤니티 게시글 댓글 기능", description = "커뮤니티 게시글에 댓글 작성(추가), 수정, 조회, 삭제 기능")
@Slf4j(topic = "댓글 생성, 수정, 삭제")
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, CommunityRepository communityRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
    }

    /* 커뮤니티 게시글 댓글 추가*/
    @Transactional
    public CommentResponseDto createComment(Long communityId, CommentRequestDto requestDto) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 커뮤니티 게시글 검증*/
        Community community = validateCommunity(communityId);

        /* 댓글 추가(생성)*/
        Comment comment = new Comment(requestDto.getCommentContent(), community, user);

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    /* 커뮤니티 게시글 댓글 수정*/
    @Transactional
    public CommentResponseDto updateComment(Long communityId, Long commentId, CommentRequestDto requestDto) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 커뮤니티 게시글, 댓글 및 댓글에 대한 유저 권한 검증*/
        Comment comment = validateCommentOwnership(communityId, commentId, user);

        comment.updateComment(requestDto.getCommentContent());

        return new CommentResponseDto(comment);
    }

    /* 커뮤니티 게시글 댓글 삭제*/
    public void deleteComment(Long communityId, Long commentId) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 커뮤니티 게시글, 댓글 및 댓글에 대한 유저 권한 검증*/
        Comment comment = validateCommentOwnership(communityId, commentId, user);

        commentRepository.delete(comment);
    }


    /* 검증 메서드 필드*/
    /*유저 정보 검증 메서드*/
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }

    /* 게시글 검증 메서드*/
    private Community validateCommunity(Long communityId) {
        return communityRepository.findByCommunityId(communityId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_POST));
    }

    /* 댓글 및 댓글에 대한 유저 권한 검증 메서드*/
    private Comment validateCommentOwnership(Long communityId, Long commentId, User user) {
        /* 게시글 검증*/
        Community community = validateCommunity(communityId);

        /* 댓글 검증*/
        Comment comment = commentRepository.findByCommunityAndCommentId(community, commentId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_COMMENT));

        /* 댓글에 대한 유저 권한 검증*/
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NOT_YOUR_COMMENT);
        }

        return comment;
    }
}
