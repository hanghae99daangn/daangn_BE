package com.sparta.market.domain.trade.service;

import com.sparta.market.domain.trade.dto.TradeRequestDto.CreateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeRequestDto.UpdateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeResponseDto.*;
import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.trade.entity.TradePostImage;
import com.sparta.market.domain.trade.repository.TradePostImageRepository;
import com.sparta.market.domain.trade.repository.TradePostRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.global.aws.service.S3UploadService;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "tradeService")
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradePostRepository tradePostRepository;
    private final S3UploadService s3UploadService;
    private final TradePostImageRepository tradePostImageRepository;


    @Transactional
    public CreateTradeResponseDto createTrade(CreateTradeRequestDto requestDto, MultipartFile[] multipartFileList, User user) throws IOException {
        if (multipartFileList == null) {
            TradePost post = requestDto.toEntity(user);
            TradePost savedPost = tradePostRepository.save(post);
            return new CreateTradeResponseDto(savedPost);
        }

        List<String> createImageUrlList = new ArrayList<>();
        List<String> createImageNameList = new ArrayList<>();

        TradePost post = requestDto.toEntity(user);

        saveImgToS3(multipartFileList, post, createImageUrlList, createImageNameList);

        TradePost savedPost = tradePostRepository.save(post);
        return new CreateTradeResponseDto(savedPost, createImageUrlList, createImageNameList);
    }

    @Transactional
    public UpdateTradeResponseDto updateTrade(Long tradeId, UpdateTradeRequestDto requestDto, MultipartFile[] multipartFileList, User user) throws IOException {
        List<String> updateImageUrlList = new ArrayList<>();
        List<String> updateImageNameList = new ArrayList<>();

        /* 원래 이미지가 없던 글 + 파일도 업로드 안한 경우 */
        if (requestDto.getImgId() == null && multipartFileList == null) {
            TradePost post = tradePostRepository.findByIdAndUser(tradeId, user).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            return new UpdateTradeResponseDto(post);
        }
        /* 이미지가 없다가, 등록하는 경우 */
        if (requestDto.getImgId() == null) {
            TradePost post = tradePostRepository.findByIdAndUser(tradeId, user).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            saveImgToS3(multipartFileList, post, updateImageUrlList, updateImageNameList);
            return new UpdateTradeResponseDto(post, updateImageUrlList, updateImageNameList);
        }
        /* 이미지가 있었고, 내용만 수정하는 경우 */
        if (multipartFileList == null) {
            TradePost post = tradePostRepository.findByIdAndUser(tradeId, user).orElseThrow(() ->
                    new CustomException(ErrorCode.NOT_EXIST_POST)
            );
            post.update(requestDto);
            return new UpdateTradeResponseDto(post);
        }
        /* 이미지를 바꾸는 경우 */
        TradePost post = tradePostRepository.findByIdAndUser(tradeId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        if (post.getPostImageList().get(0).getId() != requestDto.getImgId()) {
            throw new CustomException(ErrorCode.NOT_YOUR_IMG);
        }

        /* 이미지 삭제 */
        TradePostImage deletePostImage = tradePostImageRepository.findById(requestDto.getImgId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_IMG)
        );
        s3UploadService.deleteFile(deletePostImage.getS3name());
        tradePostImageRepository.delete(deletePostImage);

        saveImgToS3(multipartFileList, post, updateImageUrlList, updateImageNameList);

        post.update(requestDto);
        return new UpdateTradeResponseDto(post, updateImageUrlList, updateImageNameList);
    }

    public void deletePost(Long tradeId, User user) {
        TradePost post = tradePostRepository.findByIdAndUser(tradeId, user).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_YOUR_POST)
        );

        /* 게시글 삭제시, S3 서버에 저장된 이미지도 같이 삭제 */
        List<TradePostImage> postImageList = tradePostImageRepository.findAllByTradePostId(tradeId);
        for (TradePostImage postImage : postImageList) {
            /* S3name -> UUID 포함된 파일명 */
            s3UploadService.deleteFile(postImage.getS3name());
        }
        tradePostRepository.delete(post);
    }

    private void saveImgToS3(MultipartFile[] multipartFileList, TradePost tradePost, List<String> updateImageUrlList, List<String> updateImageNameList) throws IOException {
        for (MultipartFile multipartFile : multipartFileList) {
            //
            String filename = multipartFile.getOriginalFilename();
            String imageUrl = s3UploadService.saveFile(multipartFile, multipartFile.getOriginalFilename());
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

    @Transactional(readOnly = true)
    public List<GetPostListResponseDto> getAllPostList() {
        List<TradePost> postList = tradePostRepository.findAll();
        return postList.stream().map(GetPostListResponseDto::new).toList();
    }

    @Transactional
    public GetPostResponseDto getDetailPost(Long tradeId) {
        TradePost post = tradePostRepository.findById(tradeId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        post.updateHit();
        return new GetPostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public List<GetCategoryPostListResponseDto> getCategoryPostList(String category) {
        List<TradePost> categoryPostList = tradePostRepository.findAllByCategory(category);
        return categoryPostList.stream().map(GetCategoryPostListResponseDto::new).toList();
    }
}
