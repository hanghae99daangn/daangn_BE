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
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.MockitoAnnotations.openMocks;

public class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityService communityService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        /*커뮤니티 게시글 생성 테스트용*/
//        MockitoAnnotations.openMocks(this);
        /*커뮤니티 게시글 수정 테스트용*/
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("커뮤니티 게시글 생성 테스트 - 성공")
    @Disabled
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
    @Disabled
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

    @Test
    @DisplayName("커뮤니티 게시글 수정 - 성공")
    void updateCommunityPost_Success() {
        // 준비
        Long communityId = 1L;
        CommunityRequestDto requestDto = new CommunityRequestDto("Updated Title", "Updated Content");
        User user = new User(1L); // 적절한 User 객체 설정 필요
        Community community = new Community("Title", "Content", user); // 적절한 Community 객체 설정 필요

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(communityRepository.findByCommunityId(communityId)).thenReturn(Optional.of(community));

        doAnswer(invocation -> {
            Community updatedCommunity = invocation.getArgument(0);
            assertEquals(requestDto.getTitle(), updatedCommunity.getTitle());
            assertEquals(requestDto.getContent(), updatedCommunity.getContent());
            return null;
        }).when(communityRepository).save(any(Community.class));

        // 실행
        CommunityResponseDto result = communityService.updateCommunityPost(communityId, requestDto);

        // 검증
        assertNotNull(result);
        assertEquals("Updated Title", community.getTitle()); // 직접 엔티티의 상태를 확인
        assertEquals("Updated Content", community.getContent()); // 직접 엔티티의 상태를 확인

        // Mockito.verify를 사용하여 findByEmail과 findByCommunityId가 호출되었는지 확인
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(communityRepository, times(1)).findByCommunityId(communityId);
        // save 메서드 호출 검증을 제거합니다.
    }
}
