package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.CommentRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.CommentResponse;
import com.okestro.okestroonlinejudge.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글(Comment) 관련 API 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/oj")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 목록 조회.
     */
    @Operation(summary = "댓글 목록 조회", description = "특정 솔루션의 댓글 목록을 조회합니다.")
    @GetMapping("/solutions/{solutionId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable Long solutionId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "createdAt"));
        String username = userDetails != null ? userDetails.getUsername() : null;
        
        Page<CommentResponse> result = commentService.getComments(solutionId, pageable, username);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 댓글 생성.
     */
    @Operation(summary = "댓글 생성", description = "솔루션에 댓글을 작성합니다.")
    @PostMapping("/solutions/{solutionId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long solutionId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            CommentResponse result = commentService.create(solutionId, request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 댓글 수정.
     */
    @Operation(summary = "댓글 수정", description = "자신의 댓글을 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            CommentResponse result = commentService.update(commentId, request, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 댓글 삭제.
     */
    @Operation(summary = "댓글 삭제", description = "자신의 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            commentService.delete(commentId, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 댓글 좋아요 토글.
     */
    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            commentService.toggleLike(commentId, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}

