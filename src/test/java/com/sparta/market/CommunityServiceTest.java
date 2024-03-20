package com.sparta.market;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.repository.CommunityRepository;
import com.sparta.market.domain.community.service.CommunityService;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityService communityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("커뮤니티 게시글 생성 테스트 - 성공")
    void createCommunityPost_Success() {
        // 준비
        String email = "user@example.com";
        CommunityRequestDto requestDto = new CommunityRequestDto("Test Title", "Test Content");
        User user = new User(); // 유저 객체 설정 필요
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityRepository.save(any(Community.class))).thenAnswer(i -> i.getArguments()[0]);

        // 실행
        CommunityResponseDto result = communityService.createCommunityPost(requestDto);

        // 검증
        assertNotNull(result);
        assertEquals(requestDto.getTitle(), result.getTitle());
        assertEquals(requestDto.getContent(), result.getContents());

        verify(userRepository, times(1)).findByEmail(email);
        verify(communityRepository, times(1)).save(any(Community.class));
    }

    @Test
    @DisplayName("커뮤니티 게시글 생성 테스트 - 유저 정보 없음")
    void createCommunityPost_Failure_UserNotFound() {
        // 준비
        CommunityRequestDto requestDto = new CommunityRequestDto("Test Title", "Test Content");
        String nonexistentEmail = "nonexistent@example.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(nonexistentEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(nonexistentEmail)).thenReturn(Optional.empty());

        // 실행 & 검증
        assertThrows(CustomException.class, () -> communityService.createCommunityPost(requestDto));

        verify(userRepository, times(1)).findByEmail(nonexistentEmail);
        verify(communityRepository, never()).save(any(Community.class));
    }
}
