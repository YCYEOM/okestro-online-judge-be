package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.domain.ProblemStatisticsEntity;
import com.okestro.okestroonlinejudge.dto.request.ProblemCommentRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.ProblemCommentResponse;
import com.okestro.okestroonlinejudge.security.CustomUserDetails;
import com.okestro.okestroonlinejudge.service.ProblemInteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제 상호작용 컨트롤러 (좋아요, 댓글, 조회수).
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Problem Interaction", description = "문제 좋아요/댓글/조회수 API")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemInteractionController {

    private final ProblemInteractionService problemInteractionService;

    // ==================== 통계 ====================

    /**
     * 문제 통계 정보 조회 (조회수, 좋아요수, 댓글수)
     */
    @Operation(summary = "문제 통계 조회", description = "문제의 조회수, 좋아요수, 댓글수를 조회합니다.")
    @GetMapping("/{problemId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @PathVariable Long problemId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            ProblemStatisticsEntity stats = problemInteractionService.getStatistics(problemId);
            Long userId = userDetails != null ? userDetails.getUserId() : null;
            boolean isLiked = problemInteractionService.isLiked(problemId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("viewCount", stats.getViewCount());
            result.put("likeCount", stats.getLikeCount());
            result.put("commentCount", stats.getCommentCount());
            result.put("isLiked", isLiked);

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 문제 조회수 증가
     */
    @Operation(summary = "문제 조회수 증가", description = "문제 조회수를 1 증가시킵니다.")
    @PostMapping("/{problemId}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long problemId) {
        try {
            problemInteractionService.incrementViewCount(problemId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    // ==================== 좋아요 ====================

    /**
     * 문제 좋아요 토글
     */
    @Operation(summary = "문제 좋아요 토글", description = "문제 좋아요를 추가하거나 취소합니다.")
    @PostMapping("/{problemId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @PathVariable Long problemId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("로그인이 필요합니다."));
        }

        try {
            boolean isLiked = problemInteractionService.toggleLike(problemId, userDetails.getUserId());
            ProblemStatisticsEntity stats = problemInteractionService.getStatistics(problemId);

            Map<String, Object> result = new HashMap<>();
            result.put("isLiked", isLiked);
            result.put("likeCount", stats.getLikeCount());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    // ==================== 댓글 ====================

    /**
     * 문제 댓글 목록 조회 (페이징)
     */
    @Operation(summary = "문제 댓글 목록 조회", description = "문제 댓글 목록을 페이징하여 조회합니다.")
    @GetMapping("/{problemId}/comments")
    public ResponseEntity<ApiResponse<Page<ProblemCommentResponse>>> getComments(
            @PathVariable Long problemId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getUserId() : null;
        Page<ProblemCommentResponse> comments = problemInteractionService.getComments(problemId, page, size, userId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 문제 댓글 작성
     */
    @Operation(summary = "문제 댓글 작성", description = "문제에 댓글을 작성합니다.")
    @PostMapping("/{problemId}/comments")
    public ResponseEntity<ApiResponse<ProblemCommentResponse>> createComment(
            @PathVariable Long problemId,
            @RequestBody ProblemCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("로그인이 필요합니다."));
        }

        try {
            ProblemCommentResponse comment = problemInteractionService.createComment(problemId, userDetails.getUserId(), request);
            return ResponseEntity.ok(ApiResponse.success(comment, 201));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 문제 댓글 수정
     */
    @Operation(summary = "문제 댓글 수정", description = "문제 댓글을 수정합니다.")
    @PutMapping("/{problemId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<ProblemCommentResponse>> updateComment(
            @PathVariable Long problemId,
            @PathVariable Long commentId,
            @RequestBody ProblemCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("로그인이 필요합니다."));
        }

        try {
            ProblemCommentResponse comment = problemInteractionService.updateComment(commentId, userDetails.getUserId(), request);
            return ResponseEntity.ok(ApiResponse.success(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 문제 댓글 삭제
     */
    @Operation(summary = "문제 댓글 삭제", description = "문제 댓글을 삭제합니다 (소프트 삭제).")
    @DeleteMapping("/{problemId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long problemId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.unauthorized("로그인이 필요합니다."));
        }

        try {
            problemInteractionService.deleteComment(commentId, userDetails.getUserId());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
