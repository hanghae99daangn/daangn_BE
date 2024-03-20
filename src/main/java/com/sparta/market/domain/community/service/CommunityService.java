package com.sparta.market.domain.community.service;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.dto.GetAllCommunityResponseDto;
import com.sparta.market.domain.community.dto.GetCommunityResponseDto;
import com.sparta.market.domain.community.entity.Community;
import com.sparta.market.domain.community.entity.CommunityCategory;
import com.sparta.market.domain.community.entity.CommunityImage;
import com.sparta.market.domain.community.repository.CommunityImageRepository;
import com.sparta.market.domain.community.repository.CommunityRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.domain.user.repository.UserRepository;
import com.sparta.market.global.aws.service.S3UploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sparta.market.global.common.exception.ErrorCode.*;

@Slf4j(topic = "Community Service")
@Tag(name = "Community Service", description = "커뮤니티 게시글 서비스 로직 클래스")
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final CommunityImageRepository communityImageRepository;

    public CommunityService(CommunityRepository communityRepository, UserRepository userRepository, S3UploadService s3UploadService, CommunityImageRepository communityImageRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.s3UploadService = s3UploadService;
        this.communityImageRepository = communityImageRepository;
    }

    /*커뮤니티 게시글 생성 로직*/
    @Transactional
    public CommunityResponseDto createCommunityPost(CommunityRequestDto requestDto,
                                                    MultipartFile[] multipartFileList) throws IOException {
        /*유저 정보 검증*/
        User user = getAuthenticatedUser();

        /*Builder 사용 entity 객체 생성*/
        Community community = Community.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .category(requestDto.getCategory())
                .build();

        /*DB에 Community Post 정보 저장*/
        communityRepository.save(community);

        /* 이미지 파일이 없을 경우*/
        if (multipartFileList == null) {
            /*Community Post 정보 반환*/
            return new CommunityResponseDto(community);
        }

        /* 이미지 파일 정보 저장 리스트*/
        List<String> createImageUrlList = new ArrayList<>();
        List<String> createImageNameList = new ArrayList<>();

        /* image file S3 save*/
        saveImgToS3(multipartFileList, community, createImageUrlList, createImageNameList);

        /*Community Post 정보 반환*/
        return new CommunityResponseDto(community, createImageUrlList, createImageNameList);
    }

    /* 커뮤니티 게시글 수정 로직*/
    @Transactional
    public CommunityResponseDto updateCommunityPost(Long communityId, CommunityRequestDto requestDto,
                                                    MultipartFile[] multipartFileList) throws IOException {

        List<String> updateImageUrlList = new ArrayList<>();
        List<String> updateImageNameList = new ArrayList<>();

        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 게시글 및 게시글에 대한 유저 권한 검증 */
        Community community = validatePostOwnership(communityId, user);

        /* 게시글 수정 로직
        * 1. 이미지 없고, 파일도 업로드 안 한 경우
        * 2. 이미지 없고, 파일을 업로드 한 경우
        * 3. 이미지 있고, 내용만 수정하는 경우
        * 4. 이미지 변경(기존 이미지 삭제 후 새로운 이미지 저장 */
        if (requestDto.getImgId() == null && multipartFileList == null) {
            /* 1. */
            community.updatePost(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());
            /* 수정된 게시글 정보 반환*/
            return new CommunityResponseDto(community);
        } else if (requestDto.getImgId() == null) {
            /* 2. */
            community.updatePost(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());
            saveImgToS3(multipartFileList, community, updateImageUrlList, updateImageNameList);
            /* 수정된 게시글 정보 반환*/
            return new CommunityResponseDto(community, updateImageUrlList, updateImageNameList);
        } else if (multipartFileList == null) {
            /* 3. */
            community.updatePost(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());
            /* 수정된 게시글 정보 반환*/
            return new CommunityResponseDto(community);
        }

        /* 4. */
        if (!Objects.equals(community.getCommunityImages().get(0).getImageId(), requestDto.getImgId())) {
            throw new CustomException(NOT_EXIST_IMG);
        }
        CommunityImage deleteCommunityImage = communityImageRepository.findById(requestDto.getImgId())
                        .orElseThrow(() -> new CustomException(NOT_EXIST_IMG));
        s3UploadService.deleteFile(deleteCommunityImage.getImageName());
        communityImageRepository.delete(deleteCommunityImage);

        saveImgToS3(multipartFileList, community, updateImageUrlList, updateImageNameList);
        community.updatePost(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());

        return new CommunityResponseDto(community, updateImageUrlList, updateImageNameList);
    }

    /* 커뮤니티 게시글 삭제 로직*/
    @Transactional
    public void deleteCommunityPost(Long communityId) {
        /* 유저 정보 검증*/
        User user = getAuthenticatedUser();

        /* 게시글 및 게시글에 대한 유저 권한 검증 */
        Community community = validatePostOwnership(communityId, user);

        /* 게시글 삭제, S3 서버에 저장된 이미지도 같이 삭제 */
        List<CommunityImage> communityImageList = communityImageRepository.findAllByCommunityCommunityId(communityId);
        for (CommunityImage communityImage : communityImageList) {
            /* S3name -> UUID 포함된 파일명 */
            s3UploadService.deleteFile(communityImage.getS3name());
        }
        communityRepository.delete(community);
    }

    /* 선택한 커뮤니티 게시글 조회 로직*/
    @Transactional(readOnly = true)
    public GetCommunityResponseDto findCommunityPost(Long communityId) {
        /* 조회 시 유저 정보 검증 x*/
        Community community = validateCommunity(communityId);

        return new GetCommunityResponseDto(community);
    }

    /* 전체 커뮤니티 게시글 목록 조회 로직*/
    @Transactional(readOnly = true)
    public Page<GetAllCommunityResponseDto> getAllCommunity(int page, boolean isAsc) {
        /* pageable 객체 생성*/
        Pageable pageable = validateAndCreatePageable(page, isAsc);

        /* 페이징 처리*/
        Page<Community> communityPage = communityRepository.findAll(pageable);

        /* 데이터 반환*/
        return communityPage.map(GetAllCommunityResponseDto::new);
    }

    /* 카테고리 별 커뮤니티 게시글 조회 로직*/
    @Transactional(readOnly = true)
    public Page<GetAllCommunityResponseDto> getCommunityByCategory(int page, boolean isAsc, CommunityCategory category) {
        /* pageable 객체 생성*/
        Pageable pageable = validateAndCreatePageable(page, isAsc);

        /* 카테고리별 페이징 처리*/
        Page<Community> communityPage = communityRepository.findByCategory(category, pageable);

        /* 데이터 반환*/
        return communityPage.map(GetAllCommunityResponseDto::new);
    }


    /* 커뮤니티 게시글 이미지 업로드 로직*/
    private void saveImgToS3(MultipartFile[] multipartFileList, Community community,
                             List<String> updateImageUrlList, List<String> updateImageNameList) throws IOException {

        for (MultipartFile multipartFile : multipartFileList) {

            String filename = multipartFile.getOriginalFilename();
            String imageUrl = s3UploadService.saveFile(multipartFile, multipartFile.getOriginalFilename());
            CommunityImage communityImage = new CommunityImage(
                    multipartFile.getOriginalFilename(),
                    filename,
                    imageUrl,
                    community
            );
            communityImageRepository.save(communityImage);
            updateImageUrlList.add(imageUrl);
            updateImageNameList.add(multipartFile.getOriginalFilename());
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

    /* 입력 값 검증과 페이지 설정 메서드*/
    private Pageable validateAndCreatePageable(int page, boolean isAsc) {
        if (page < 0) {
            throw new CustomException(VALIDATION_ERROR);
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, 30, Sort.by(direction, "createdAt"));
    }
}
