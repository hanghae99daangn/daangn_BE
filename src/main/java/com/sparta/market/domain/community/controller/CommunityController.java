package com.sparta.market.domain.community.controller;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.dto.GetCommunityResponseDto;
import com.sparta.market.domain.community.dto.UpdateCommunityRequestDto;
import com.sparta.market.domain.community.entity.CommunityCategory;
import com.sparta.market.domain.community.service.CommunityService;
import com.sparta.market.global.common.dto.ResponseDto;
import com.sparta.market.global.common.exception.CustomException;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.sparta.market.global.common.exception.ErrorCode.INVALID_CATEGORY_INPUT;

@Slf4j(topic = "Community Controller")
@Tag(name = "Community Controller", description = "커뮤니티 게시글 컨트롤러")
@RestController
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Operation(summary = "커뮤니티 게시글 등록", description = "커뮤니티 게시글을 등록합니다.")
    @PostMapping(value = "/community", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    /*@RequestPart 사용 이미지 업로드 추가 리팩토링 예정*/
    public ResponseEntity<?> createCommunityPost(@RequestPart(value = "files", required = false)MultipartFile[] multipartFilesList,
                                                 @RequestPart(value = "CommunityRequestDto") CommunityRequestDto requestDto) throws IOException, java.io.IOException {

        CommunityResponseDto responseDto = communityService.createCommunityPost(requestDto, multipartFilesList);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 글 작성 성공", responseDto));
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "등록한 커뮤니티 게시글의 내용을 수정할 수 있습니다.")
    @PostMapping(value = "/community/{communityId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    /*@RequestPart 사용 업로드 이미지 수정 리팩토링 예정*/
    public ResponseEntity<?> updateCommunityPost(@PathVariable Long communityId,
                                                 @RequestPart(value = "files", required = false) MultipartFile[] multipartFilesList,
                                                 @RequestPart(value = "UpdateCommunityRequestDto") UpdateCommunityRequestDto requestDto) throws IOException, java.io.IOException {

        CommunityResponseDto responseDto = communityService.updateCommunityPost(communityId, requestDto, multipartFilesList);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 글 수정 성공", responseDto));
    }

    @Operation(summary = "커뮤니티 게시글 삭제", description = "등록한 커뮤니티 게시글을 삭제합니다.")
    @DeleteMapping("/community/{communityId}")
    public ResponseEntity<?> deleteCommunityPost(@PathVariable Long communityId) {

        communityService.deleteCommunityPost(communityId);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 글 삭제 성공", "엘든링"));
    }

    @Operation(summary = "선택한 커뮤니티 게시글 조회", description = "선택한 커뮤니티 게시글의 정보를 조회합니다.")
    @GetMapping("/community/{communityId}")
    public ResponseEntity<?> findCommunityPost(@PathVariable Long communityId) {

        GetCommunityResponseDto responseDto = communityService.findCommunityPost(communityId);

        return ResponseEntity.ok().body(ResponseDto.success("선택한 게시글 조회 성공", responseDto));
    }

    @Operation(summary = "전체 커뮤니티 게시글 조회", description = "전체 커뮤니티 게시글 목록을 조회합니다, 카테고리별 필터링 기능도 사용할 수 있습니다.")
    @GetMapping("/community")
    public ResponseEntity<?> getAllCommunity(@RequestParam("isAsc") boolean isAsc,
                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "category", required = false) String categoryName) {

        /* 카테고리 필터링을 했을 때*/
        if (categoryName != null && !categoryName.isEmpty()) {
            try {
                /* 카테고리 입력 시 카테고리 별 조회 처리*/
                CommunityCategory category = CommunityCategory.valueOf(categoryName.toUpperCase());

                return ResponseEntity.ok().body(
                        ResponseDto.success("카테고리별 커뮤니티 게시글 조회 성공",
                                communityService.getCommunityByCategory(page - 1, isAsc, category)));
            } catch (CustomException e) {
                /* 잘못된 카테고리 값 입력에 대한 처리*/
                return ResponseEntity.badRequest().body(ResponseDto.error(INVALID_CATEGORY_INPUT.getKey(), INVALID_CATEGORY_INPUT.getMessage(), categoryName));
            }
        }

        /* 카테고리 입력 없을 때 전체 게시글 목록 조회 처리*/
        return ResponseEntity.ok().body(
                ResponseDto.success("전체 커뮤니티 게시글 조회 성공",
                        communityService.getAllCommunity(page - 1, isAsc)));
    }
}
