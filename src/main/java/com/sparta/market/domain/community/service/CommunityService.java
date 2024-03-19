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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.market.global.common.exception.ErrorCode.NOT_EXIST_USER;

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
    public CommunityResponseDto createCommunityPost(CommunityRequestDto requestDto, UserDetails userDetails) {
        /*유저 정보 검증*/
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));

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
}
