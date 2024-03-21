package com.sparta.market;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.dto.GetAllCommunityResponseDto;
import com.sparta.market.domain.community.dto.GetCommunityResponseDto;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
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
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
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
        MockitoAnnotations.openMocks(this);
        /*커뮤니티 게시글 수정 테스트용*/
//        closeable = openMocks(this);
    }

    @Test
    @DisplayName("커뮤니티 게시글 생성 테스트 - 성공")
    @Disabled
    void createCommunityPost_Success() throws IOException {
        // 준비
        String email = "user@example.com";
        CommunityRequestDto requestDto = new CommunityRequestDto("Test Title", "Test Content", CommunityCategory.동네질문, "Test Address");
        User user = new User(); // 유저 객체 설정 필요
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityRepository.save(any(Community.class))).thenAnswer(i -> i.getArguments()[0]);

        // 실행
        CommunityResponseDto result = communityService.createCommunityPost(requestDto, null);

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
        CommunityRequestDto requestDto = new CommunityRequestDto("Test Title", "Test Content", CommunityCategory.동네질문, "Test Address");
        String nonexistentEmail = "nonexistent@example.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(nonexistentEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(nonexistentEmail)).thenReturn(Optional.empty());

        // 실행 & 검증
        assertThrows(CustomException.class, () -> communityService.createCommunityPost(requestDto, null));

        verify(userRepository, times(1)).findByEmail(nonexistentEmail);
        verify(communityRepository, never()).save(any(Community.class));
    }

    @Test
    @DisplayName("커뮤니티 게시글 수정 - 성공")
    @Disabled
    void updateCommunityPost_Success() throws IOException {
        // 준비
        Long communityId = 1L;
        CommunityRequestDto requestDto = new CommunityRequestDto("Updated Title", "Updated Content", CommunityCategory.동네질문, "Test Address");
        User user = new User(1L); // 적절한 User 객체 설정 필요
        Community community = new Community("Title", "Content", user, CommunityCategory.동네질문, "Test Address"); // 적절한 Community 객체 설정 필요

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
        CommunityResponseDto result = communityService.updateCommunityPost(communityId, requestDto, null);

        // 검증
        assertNotNull(result);
        assertEquals("Updated Title", community.getTitle()); // 직접 엔티티의 상태를 확인
        assertEquals("Updated Content", community.getContent()); // 직접 엔티티의 상태를 확인

        // Mockito.verify를 사용하여 findByEmail과 findByCommunityId가 호출되었는지 확인
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(communityRepository, times(1)).findByCommunityId(communityId);
        // save 메서드 호출 검증을 제거합니다.
    }

    @Test
    @DisplayName("커뮤니티 게시글 삭제 - 성공")
    @Disabled
    void deleteCommunityPost_Success() {
        // 준비
        Long communityId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        Community community = mock(Community.class);

        when(user.getId()).thenReturn(userId);
        when(community.getUser()).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(communityRepository.findByCommunityId(communityId)).thenReturn(Optional.of(community));

        // 실행
        communityService.deleteCommunityPost(communityId);

        // 검증
        verify(communityRepository).delete(community);
    }

    @Test
    @DisplayName("전체 커뮤니티 게시글 목록 조회 - 성공")
    @Disabled
    void getAllCommunity_Success() {
        // 준비
        User user = new User(1L, "nickName");
        int page = 0;
        boolean isAsc = true;
        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.ASC, "createdAt"));
        // Community 객체 생성 (빌더 패턴이나 적절한 생성자 사용)
        Community community = new Community(1L, "Sample Title", "Sample content", user, CommunityCategory.동네질문, "Test Address");
        Page<Community> communityPage = new PageImpl<>(Collections.singletonList(community), pageable, 1);
        when(communityRepository.findAll(any(Pageable.class))).thenReturn(communityPage);

        // 실행
        Page<GetAllCommunityResponseDto> result = communityService.getAllCommunity(page, isAsc);

        // 검증
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(communityRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("전체 커뮤니티 게시글 목록 조회 - 페이지 값이 음수일 때 실패")
    @Disabled
    void getAllCommunity_WhenPageIsNegative_ThrowsCustomException() {
        // 준비
        int page = -1; // 음수 페이지
        boolean isAsc = true;

        // 실행 & 검증
        assertThrows(CustomException.class, () -> communityService.getAllCommunity(page, isAsc));
    }

    @Test
    @DisplayName("선택한 커뮤니티 게시글 조회 - 성공")
    @Disabled
    void findCommunityPost_Success() {
        // 준비
        User user = new User(1L, "nickName");
        Long communityId = 1L;
        Community mockCommunity = new Community(communityId, "Test Title", "Test Content", user, CommunityCategory.동네질문, "test address");
        // 사용자 정보나 다른 필요한 필드도 여기에 추가합니다.

        when(communityRepository.findByCommunityId(communityId)).thenReturn(Optional.of(mockCommunity));

        // 실행
        GetCommunityResponseDto result = communityService.findCommunityPost(communityId);

        // 검증
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContents());
        assertEquals("nickName", result.getNickname());
        // 다른 필드 검증도 필요한 경우 여기에 추가합니다.

        verify(communityRepository).findByCommunityId(communityId);
    }
}
