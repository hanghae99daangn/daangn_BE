package com.sparta.market.domain.community.controller;

import com.sparta.market.domain.community.dto.CommunityRequestDto;
import com.sparta.market.domain.community.dto.CommunityResponseDto;
import com.sparta.market.domain.community.service.CommunityService;
import com.sparta.market.global.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "Community Controller")
@Tag(name = "Community Controller", description = "커뮤니티 게시글 컨트롤러")
@RestController
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping("/community")
    /*@RequestPart 사용 이미지 업로드 추가 리팩토링 예정*/
    public ResponseEntity<?> createCommunityPost(@RequestBody CommunityRequestDto requestDto) {

        CommunityResponseDto responseDto = communityService.createCommunityPost(requestDto);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 글 작성 성공", responseDto));
    }

    @PostMapping("/community/{communityId}")
    /*@RequestPart 사용 업로드 이미지 수정 리팩토링 예정*/
    public ResponseEntity<?> updateCommunityPost(@PathVariable Long communityId,
                                                 @RequestBody CommunityRequestDto requestDto) {

        CommunityResponseDto responseDto = communityService.updateCommunityPost(communityId, requestDto);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 글 수정 성공", responseDto));
    }
}
