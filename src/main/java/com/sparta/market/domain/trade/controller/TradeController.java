package com.sparta.market.domain.trade.controller;

import com.sparta.market.domain.trade.dto.TradeRequestDto.CreateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeRequestDto.UpdateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeResponseDto.*;
import com.sparta.market.domain.trade.service.TradeService;
import com.sparta.market.global.common.dto.ResponseDto;
import com.sparta.market.global.security.config.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "거래글 API", description = "거래글 CRUD")
@Slf4j(topic = "거래글 로그")
@RestController
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @Operation(summary = "게시글 등록",
            description = "게시글 등록 -> add String Item을 클릭해서 이미지를 업로드하세요!")
    @PostMapping(value = "/trades", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createTrade(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                         @Valid @RequestPart(value = "createTradeRequestDto") CreateTradeRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        CreateTradeResponseDto responseDto = tradeService.createTrade(requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "게시글 수정",
            description = "게시글 수정: title, contents, category, file")
    @PostMapping(value = "/trades/{tradeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateTrade(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                         @Valid @RequestPart(value = "updateTradeRequestDto") UpdateTradeRequestDto requestDto,
                                         @PathVariable Long tradeId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        UpdateTradeResponseDto responseDto = tradeService.updateTrade(tradeId, requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "게시글 삭제",
            description = "유저 정보가 일치할 경우, 게시글 삭제 가능")
    @DeleteMapping("/trades/{tradeId}")
    public ResponseEntity<?> deleteTrade(@PathVariable Long tradeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        tradeService.deletePost(tradeId, userDetails.getUser());
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }

    @Operation(summary = "판매글 전체 조회",
            description = "조회시, 글에 저장된 첫 번째 이미지 출력를 출력합니다!")
    @GetMapping("/trades")
    public ResponseEntity<?> getAllPostList(@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        Page<GetPostListResponseDto> postList = tradeService.getAllPostList(page);
        return ResponseEntity.ok().body(postList);
//        return postList; Page<GetPostListResponseDto>
    }
// Page<GetPostListResponseDto>
    @Operation(summary = "판매글 상세 조회",
            description = "조회시, 판매글의 Id를 입력하세요!")
    @GetMapping("/trades/{tradeId}")
    public ResponseEntity<?> getDetailPost(@PathVariable Long tradeId) {
        GetPostResponseDto responseDto = tradeService.getDetailPost(tradeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "카테고리별 판매글 전체 조회",
            description = "조회시, 글에 저장된 첫 번째 이미지 출력를 출력합니다!")
    @GetMapping("/trades/category")
    public ResponseEntity<?> getCategoryPostList(@RequestParam String category, @RequestParam(value = "page", required = false, defaultValue = "1") int page){
        Page<GetCategoryPostListResponseDto> categoryList = tradeService.getCategoryPostList(category, page);
        return ResponseEntity.ok().body(categoryList);
//        return ResponseEntity.ok().body(categoryList);
    }

    @Operation(summary = "좋아요 기능",
            description = "해당 API 호출시, true, false 반환")
    @PostMapping("/trades/likes/{tradeId}")
    public ResponseEntity<?> updateLike(@PathVariable Long tradeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean check = tradeService.updateLike(tradeId, userDetails.getUser());
        return ResponseEntity.ok().body(check);
    }
}
