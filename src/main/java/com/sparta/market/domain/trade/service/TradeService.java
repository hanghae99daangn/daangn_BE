package com.sparta.market.domain.trade.service;

import com.sparta.market.domain.trade.dto.TradeRequestDto.CreateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeRequestDto.UpdateTradeRequestDto;
import com.sparta.market.domain.trade.dto.TradeResponseDto.*;
import com.sparta.market.domain.trade.entity.LikeStateEnum;
import com.sparta.market.domain.trade.entity.TradeLike;
import com.sparta.market.domain.trade.entity.TradePost;
import com.sparta.market.domain.trade.entity.TradePostImage;
import com.sparta.market.domain.trade.repository.TradeLikeRepository;
import com.sparta.market.domain.trade.repository.TradePostImageRepository;
import com.sparta.market.domain.trade.repository.TradePostRepository;
import com.sparta.market.domain.user.entity.User;
import com.sparta.market.global.aws.service.S3UploadService;
import com.sparta.market.global.common.exception.CustomException;
import com.sparta.market.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "tradeService")
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradePostRepository tradePostRepository;
    private final S3UploadService s3UploadService;
    private final TradePostImageRepository tradePostImageRepository;
    private final TradeLikeRepository tradeLikeRepository;


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
        log.info(user.getId().toString());

        TradePost tradePost = tradePostRepository.findById(tradeId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );

        /* 기존에 이미지가 없었던 경우 */
        if (requestDto.getImgId() == null) {
            tradePost.update(requestDto);

            /* 이미지를 새로 등록하는 경우 */
            if (multipartFileList != null) {
                saveImgToS3(multipartFileList, tradePost, updateImageUrlList, updateImageNameList);
                return new UpdateTradeResponseDto(tradePost, updateImageUrlList, updateImageNameList);
            } else {
                /* 이미지 등록이 없는 경우 */
                return new UpdateTradeResponseDto(tradePost);
            }
        } else { /* 이미지가 있는 경우 */
            List<TradePostImage> imageList = tradePostImageRepository.findAllByTradePostId(tradeId);

            tradePost.update(requestDto);

            /* 이미지를 바꾸는 경우 */
            if (multipartFileList != null) {
                /* 기존의 이미지 제거 */
                for (TradePostImage postImage : imageList) {
                    s3UploadService.deleteFile(postImage.getS3name());
                    tradePostImageRepository.delete(postImage);
                }
                saveImgToS3(multipartFileList, tradePost, updateImageUrlList, updateImageNameList);
                return new UpdateTradeResponseDto(tradePost, updateImageUrlList, updateImageNameList);
            } else {
                return new UpdateTradeResponseDto(tradePost);
            }
        }
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

    @Transactional(readOnly = true)
    public Page<GetPostListResponseDto> getAllPostList(int page) {
        int pageNum = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNum, 30);
        Page<TradePost> postList = tradePostRepository.findAll(pageable);
        return postList.map(GetPostListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<GetPostListResponseDto> getAllPostListByUser(int page, User user) {
        int pageNum = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNum, 30);
        String dong = extractDong(user.getAddress());
        Page<TradePost> postList = tradePostRepository.findAllByContactPlaceContaining(dong, pageable);
        return postList.map(GetPostListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<GetCategoryPostListResponseDto> getCategoryPostList(String category, int page, User user) {
        int pageNum = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNum, 30);
        String dong = extractDong(user.getAddress());
        Page<TradePost> categoryPostList = tradePostRepository.findAllByContactPlaceContainingAndCategory(dong, category, pageable);
        return categoryPostList.map(GetCategoryPostListResponseDto::new);
    }

    @Transactional
    public GetPostResponseDto getDetailPost(Long tradeId) {
        TradePost post = getTradePostById(tradeId);
        post.updateHit();
        return new GetPostResponseDto(post);
    }

    @Transactional
    public boolean updateLike(Long tradeId, User user) {
        boolean likeCheck = tradeLikeRepository.existsByUserAndTradePostId(user, tradeId);
        TradePost post = getTradePostById(tradeId);
        /* 좋아요 로직 (이미 누른 경우와 누르지 않았던 경우) */
        if (likeCheck) {
            Optional<TradeLike> like = tradeLikeRepository.findByUserAndTradePostId(user, tradeId);
            TradeLike getLike = like.orElseThrow(()->
                    new CustomException(ErrorCode.VALIDATION_ERROR)
            );
            getLike.update();
            return getLike.getLikeState().getState().equals("활성화");
        } else {
            TradeLike like = TradeLike.builder()
                    .likeState(LikeStateEnum.ENABLED)
                    .user(user)
                    .tradePost(post)
                    .build();
            tradeLikeRepository.save(like);
            return true;
        }
    }


    /* 메서드 모음 */
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

    private TradePost getTradePostById(Long tradeId) {
        TradePost post = tradePostRepository.findById(tradeId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXIST_POST)
        );
        return post;
    }

    private String extractDong(String address) {
        String dong = "";
        Pattern pattern = Pattern.compile("(\\S+[동])");
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            dong = matcher.group();
        }
        return dong;
    }
}
