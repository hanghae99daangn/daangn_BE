package com.sparta.market.domain.trade.controller;

import com.sparta.market.domain.trade.dto.TradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeResponseDto.CreatePostResponseDto;
import com.sparta.market.domain.trade.service.TradeService;
import com.sparta.market.global.common.dto.ResponseDto;
import com.sparta.market.global.security.config.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j(topic = "게시글 등록 로그")
@RestController
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/trades")
    public ResponseEntity<?> createTrade(@RequestPart(value = "files", required = false) MultipartFile[] multipartFileList,
                                         @RequestPart(value = "createTradeRequestDto") TradeRequestDto.CreateTradeRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        CreatePostResponseDto responseDto = tradeService.createTrade(requestDto, multipartFileList, userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("등록 성공", responseDto));
    }
}
