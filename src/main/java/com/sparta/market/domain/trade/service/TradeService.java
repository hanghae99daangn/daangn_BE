package com.sparta.market.domain.trade.service;

import com.sparta.market.domain.trade.dto.TradeRequestDto.CreateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeResponseDto.CreatePostResponseDto;
import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.trade.entity.TradePostImage;
import com.sparta.market.domain.trade.repository.TradePostImageRepository;
import com.sparta.market.domain.trade.repository.TradePostRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.global.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j(topic = "tradeService")
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradePostRepository tradePostRepository;
    private final S3UploadService s3UploadService;
    private final TradePostImageRepository tradePostImageRepository;


    @Transactional
    public CreatePostResponseDto createTrade(CreateTradeRequestDto requestDto, MultipartFile[] multipartFileList, User user) throws IOException {
        if (multipartFileList == null || multipartFileList.length == 0) {
            TradePost post = requestDto.toEntity(user);
            TradePost savedPost = tradePostRepository.save(post);
            return new CreatePostResponseDto(savedPost);
        }

        List<String> createImageUrlList = new ArrayList<>();
        List<String> createImageNameList = new ArrayList<>();

        TradePost post = requestDto.toEntity(user);

        saveImgToS3(multipartFileList, post, createImageUrlList, createImageNameList);

        TradePost savedPost = tradePostRepository.save(post);
        return new CreatePostResponseDto(savedPost, createImageUrlList, createImageNameList);
    }

    private void saveImgToS3(MultipartFile[] multipartFileList, TradePost tradePost, List<String> updateImageUrlList, List<String> updateImageNameList) throws IOException {
        for (MultipartFile multipartFile : multipartFileList) {
            String filename = UUID.randomUUID() + multipartFile.getOriginalFilename();
            String imageUrl = s3UploadService.saveFile(multipartFile, filename);
            TradePostImage postImage = TradePostImage.builder()
                    .url(imageUrl)
                    .imageName(multipartFile.getOriginalFilename())
                    .s3name(filename)
                    .tradePost(tradePost)
                    .build();
            tradePostImageRepository.save(postImage);
            updateImageUrlList.add(imageUrl);
            updateImageNameList.add(multipartFile.getOriginalFilename());
        }
    }
}
