package com.sparta.market.domain.community.service;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.repository.CommunityRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.market.global.common.exception.ErrorCode.*;

@Slf4j(topic = "Community Service")
@Tag(name = "Community Service", description = "커뮤니티 게시글 서비스 로직 클래스")
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public CommunityService(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    /*커뮤니티 게시글 생성 로직*/
    @Transactional
    public CommunityResponseDto createCommunityPost(CommunityRequestDto requestDto) {
        /*유저 정보 검증*/
        User user = getAuthenticatedUser();

        /*Builder 사용 entity 객체 생성*/
        Community community = Community.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();

        /*DB에 Community Post 정보 저장*/
        communityRepository.save(community);

        /*Community Post 정보 반환*/
        return new CommunityResponseDto(community);
    }

    /* 커뮤니티 게시글 수정 로직*/
    @Transactional
    public CommunityResponseDto updateCommunityPost(Long communityId, CommunityRequestDto requestDto) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 게시글 및 게시글에 대한 유저 권한 검증 */
        Community community = validatePostOwnership(communityId, user);

        /* 게시글 수정*/
        community.updatePost(requestDto.getTitle(), requestDto.getContent());

        /* 수정된 게시글 정보 반환*/
        return new CommunityResponseDto(community);
    }

    /* 커뮤니티 게시글 삭제 로직*/
    @Transactional
    public void deleteCommunityPost(Long communityId) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 게시글 및 게시글에 대한 유저 권한 검증 */
        Community community = validatePostOwnership(communityId, user);

        /* 게시글 삭제*/
        communityRepository.delete(community);
    }

    /* 선택한 커뮤니티 게시글 조회 로직*/
    @Transactional(readOnly = true)
    public CommunityResponseDto findCommunityPost(Long communityId) {
        /* 조회 시 유저 정보 검증 x*/
        Community community = validateCommunity(communityId);

        return new CommunityResponseDto(community);
    }

    /* 전체 커뮤니티 게시글 목록 조회 로직*/
    @Transactional(readOnly = true)
    public Page<CommunityResponseDto> getAllCommunity(int page, boolean isAsc) {
        /* 입력 값 유효성 검증*/
        try {
            if (page < 0) {
                throw new CustomException(VALIDATION_ERROR);
            }

            /* 정렬 기준 선택, 페이지 크기 선택*/
            Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, "createdAt");
            Pageable pageable = PageRequest.of(page, 30, sort);

            /* 페이징 처리*/
            Page<Community> communityPage = communityRepository.findAll(pageable);

            /* 데이터 반환*/
            return communityPage.map(CommunityResponseDto::new);
        } catch (CustomException e) {

            /* 유효하지 않은 정렬 필드 및 페이지 인덱스에 대한 예외 처리*/
            throw new CustomException(VALIDATION_ERROR);
        }
    }

    /* 검증 메서드 필드*/
    /*유저 정보 검증 메서드*/
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }

    /* 게시글 및 게시글에 대한 유저 권한 검증 메서드*/
    private Community validatePostOwnership(Long communityId, User user) {
        /*게시글 정보 검증*/
        Community community = communityRepository.findByCommunityId(communityId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_POST));

        /* 게시글에 대한 유저 권한 검증*/
        if (!community.getUser().getId().equals(user.getId())) {
            throw new CustomException(NOT_YOUR_POST);
        }

        return community;
    }

    /* 게시글 검증 메서드*/
    private Community validateCommunity(Long communityId) {
        return communityRepository.findByCommunityId(communityId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_POST));
    }
}
