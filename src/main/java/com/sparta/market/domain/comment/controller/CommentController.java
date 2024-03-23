package com.sparta.market.domain.comment.controller;

import com.sparta.market.domain.comment.dto.CommentRequestDto;
import com.sparta.market.domain.comment.dto.CommentResponseDto;
import com.sparta.market.domain.comment.service.CommentService;
import com.sparta.market.global.security.config.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(summary = "Create Comment", description = "커뮤니티 게시글에 댓글을 등록합니다, 대댓글이 아닐 경우 parentId 값은 null 로 입력.")
    public ResponseEntity<?> createComment(@PathVariable Long communityId, @RequestBody CommentRequestDto requestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommentResponseDto responseDto = commentService.createComment(communityId, requestDto, userDetails);

        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/{communityId}/comment/{commentId}")
    @Operation(summary = "Update Comment", description = "커뮤니티 게시글의 댓글을 수정합니다, 대댓글이 아닐 경우 parentId 값은 null 로 입력.")
    public ResponseEntity<?> updateComment(@PathVariable Long communityId,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentRequestDto requestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommentResponseDto responseDto = commentService.updateComment(communityId, commentId, requestDto, userDetails);

        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/{communityId}/comment/{commentId}")
    @Operation(summary = "Delete Comment", description = "커뮤니티 게시글의 댓글을 삭제합니다, 대댓글이 있을 경우 댓글 내용이 삭제된 댓글입니다로 변경 됨.")
    public ResponseEntity<?> deleteComment(@PathVariable Long communityId, @PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(communityId, commentId, userDetails);

        return ResponseEntity.ok().body("커뮤니티 댓글 삭제 완료");
    }

    @DeleteMapping("/{communityId}/comment/{commentId}/child/{childCommentId}")
    @Operation(summary = "Delete Child Comment", description = "커뮤니티 게시글의 대댓글을 삭제합니다, 커뮤니티 아이디, 부모 댓글 아이디, 대댓글 아이디 입력 필요")
    public ResponseEntity<?> deleteChildComment(@PathVariable Long communityId,
                                                @PathVariable Long commentId,
                                                @PathVariable Long childCommentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteChildComment(childCommentId, userDetails);
        return ResponseEntity.ok().body("대댓글 삭제 완료");
    }

    @GetMapping("/{communityId}/comments")
    @Operation(summary = "Get Comments", description = "커뮤티니 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<?> getComments(@PathVariable Long communityId,
                                         @RequestParam("isAsc") boolean isAsc,
                                         @RequestParam(value = "page", defaultValue = "1") int page) {

        return ResponseEntity.ok().body(commentService.getComments(communityId, page -1, isAsc));
    }
}
