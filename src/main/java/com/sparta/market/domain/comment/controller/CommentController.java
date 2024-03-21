package com.sparta.market.domain.comment.controller;

import com.sparta.market.domain.comment.dto.CommentRequestDto;
import com.sparta.market.domain.comment.dto.CommentResponseDto;
import com.sparta.market.domain.comment.service.CommentService;
import com.sparta.market.global.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment Controller", description = "댓글 기능 컨트롤러")
@Slf4j(topic = "댓글 컨트롤러")
@RestController
@RequestMapping("/community")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{communityId}/comment")
    @Operation(summary = "Create Comment", description = "커뮤니티 게시글에 댓글을 등록합니다.")
    public ResponseEntity<?> createComment(@PathVariable Long communityId, @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = commentService.createComment(communityId, requestDto);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 댓글 작성 완료", responseDto));
    }

    @PutMapping("/{communityId}/comment/{commentId}")
    @Operation(summary = "Update Comment", description = "커뮤니티 게시글의 댓글을 수정합니다.")
    public ResponseEntity<?> updateComment(@PathVariable Long communityId, @PathVariable Long commentId,
                                           @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = commentService.updateComment(communityId, commentId, requestDto);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 댓글 수정 완료", responseDto));
    }

    @DeleteMapping("/{communityId}/comment/{commentId}")
    @Operation(summary = "Delete Comment" , description = "커뮤니티 게시글의 댓글을 삭제합니다.")
    public ResponseEntity<?> deleteComment(@PathVariable Long communityId, @PathVariable Long commentId) {

        commentService.deleteComment(communityId, commentId);

        return ResponseEntity.ok().body(ResponseDto.success("커뮤니티 댓글 삭제 완료", "엘든링"));
    }
}
