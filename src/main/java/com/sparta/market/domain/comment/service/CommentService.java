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
import com.sparta.market.global.security.config.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public CommentResponseDto createComment(Long communityId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        /* 유저 정보 검증*/
        User user = findAuthenticatedUser(userDetails);

        /* 커뮤니티 게시글 검증*/
        Community community = validateCommunity(communityId);

        /* 부모 댓글 여부 확인*/
        Comment parentComment = null;
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new CustomException(NOT_EXIST_COMMENT));
        }

        /* 댓글 추가(생성)*/
        Comment comment = Comment.builder()
                .commentContent(requestDto.getCommentContent())
                .community(community)
                .user(user)
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    /* 커뮤니티 게시글 댓글 수정*/
    @Transactional
    public CommentResponseDto updateComment(Long communityId, Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        /* 유저 정보 검증*/
        User user = findAuthenticatedUser(userDetails);

        /* 커뮤니티 게시글, 댓글 및 댓글에 대한 유저 권한 검증*/
        Comment comment = validateCommentOwnership(communityId, commentId, user);

        comment.updateComment(requestDto.getCommentContent());

        return new CommentResponseDto(comment);
    }

    /* 커뮤니티 게시글 댓글 삭제 - 부모 댓글*/
    @Transactional
    public void deleteComment(Long communityId, Long commentId, UserDetailsImpl userDetails) {
        /* 유저 정보 검증*/
        User user = findAuthenticatedUser(userDetails);

        /* 커뮤니티 게시글, 댓글 및 댓글에 대한 유저 권한 검증*/
        Comment comment = validateCommentOwnership(communityId, commentId, user);

        /* 대댓글이 있는 부모 댓글인 경우, 내용만 "삭제된 댓글입니다"로 변경*/
        if (!comment.getChildComments().isEmpty()) {
            comment.markAsDeleted();
        } else {
            /* 자식 댓글이 없는 경우(단독 댓글이거나 모든 자식 댓글이 이미 삭제된 상태), 댓글을 실제로 삭제*/
            commentRepository.delete(comment);
        }
    }

    /* 커뮤니티 게시글 댓글 삭제 - 자식 댓글(대댓글)*/
    @Transactional
    public void deleteChildComment(Long childCommentId, UserDetailsImpl userDetails) {
        User user = findAuthenticatedUser(userDetails);

        /* 대댓글 조회*/
        Comment childComment = commentRepository.findById(childCommentId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_COMMENT));

        /* 대댓글의 작성자가 현재 사용자와 일치하는지 확인*/
        if (!childComment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NOT_YOUR_COMMENT);
        }

        /* 자식 댓글(대댓글) 삭제*/
        commentRepository.delete(childComment);

        /* 부모 댓글이 삭제 상태일 경우 자식 댓글이 모두 삭제되면 부모 댓글을 DB에서 삭제*/
        Comment parentComment = childComment.getParentComment();
        if (parentComment != null && parentComment.isDeleted()) {
            /* 모든 자식 댓글이 삭제되었는지 확인 */
            long remainingChildren = parentComment.getChildComments().stream()
                    .filter(c -> !c.getCommentId().equals(childCommentId) && !c.isDeleted())
                    .count();

            if (remainingChildren == 0) {
                commentRepository.delete(parentComment);
            }
        }
    }


    /* 커뮤니티 게시글 댓글 목록 조회*/
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComments(Long communityId, int page, boolean isAsc) {
        /* 조회 시에는 유저 정보 검증 x */
        /* 커뮤니티 게시글 검증*/
        Community community = validateCommunity(communityId);

        /* pageable 객체 생성*/
        Pageable pageable = validateAndCreatePageable(page, isAsc);

        /* 페이징 처리*/
        Page<Comment> commentPage = commentRepository.findByCommunityAndParentCommentIsNull(community, pageable);

        /* 대댓글 정보를 포함하여 DTO 변환*/
        return commentPage.map(comment -> {
            List<CommentResponseDto> childComments = comment.getChildComments().stream()
                    .map(child -> new CommentResponseDto(child, Collections.emptyList())) // 대댓글의 대댓글은 고려하지 않음
                    .collect(Collectors.toList());
            return new CommentResponseDto(comment, childComments);
        });
    }


    /* 검증 메서드 필드*/
    /*유저 정보 검증 메서드*/
    private User findAuthenticatedUser(UserDetailsImpl userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
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

    /* 입력 값 검증과 페이지 설정 메서드*/
    private Pageable validateAndCreatePageable(int page, boolean isAsc) {
        if (page < 0) {
            throw new CustomException(VALIDATION_ERROR);
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, 10, Sort.by(direction, "createdAt"));
    }
}
