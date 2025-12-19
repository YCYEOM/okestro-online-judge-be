package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.CreateSolutionRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateSolutionRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.SolutionDetailResponse;
import com.okestro.okestroonlinejudge.dto.response.SolutionResponse;
import com.okestro.okestroonlinejudge.service.SolutionService;
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
 * 정답 코드 공유(Solution) 관련 API 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Solution", description = "정답 코드 공유 API")
@RestController
@RequestMapping("/oj/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    /**
     * 정답 공유 목록 조회.
     *
     * @param problemId 문제 ID
     * @param language  언어 (선택)
     * @param pageNumber 페이지 번호 (0부터 시작)
     * @param pageSize   페이지 크기
     * @param sort       정렬 기준 (latest, likes, comments)
     * @param userDetails 로그인 사용자 정보 (선택)
     * @return 솔루션 목록
     */
    @Operation(summary = "정답 공유 목록 조회", description = "특정 문제의 정답 공유 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SolutionResponse>>> getSolutions(
            @RequestParam Long problemId,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "latest") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Sort sortObj = switch (sort) {
            case "likes" -> Sort.by(Sort.Direction.DESC, "likeCount"); // TODO: likeCount 컬럼이나 별도 정렬 로직 필요
            case "comments" -> Sort.by(Sort.Direction.DESC, "commentCount"); // TODO: commentCount
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        
        // 현재 엔티티 필드만으로 정렬 가능한 것은 createdAt 뿐.
        // 좋아요 순 정렬 등을 위해서는 QueryDSL이나 서브쿼리, 혹은 @Formula 필드가 필요함.
        // 일단 기본 최신순 정렬만 지원하고, 추후 개선.
        if (!sort.equals("latest")) {
             // 임시: 다른 정렬도 최신순으로 처리 (에러 방지)
             sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);
        String username = userDetails != null ? userDetails.getUsername() : null;
        
        Page<SolutionResponse> result = solutionService.getSolutions(problemId, language, pageable, username);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 정답 공유 상세 조회.
     *
     * @param id          솔루션 ID
     * @param userDetails 로그인 사용자 정보 (선택)
     * @return 솔루션 상세 정보
     */
    @Operation(summary = "정답 공유 상세 조회", description = "정답 공유 상세 정보를 조회합니다. (권한 체크 포함)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> getSolution(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        try {
            SolutionDetailResponse result = solutionService.getSolution(id, username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 정답 공유 생성.
     *
     * @param request     생성 요청 DTO
     * @param userDetails 로그인 사용자 정보
     * @return 생성된 솔루션 상세 정보
     */
    @Operation(summary = "정답 공유 생성", description = "자신의 정답 제출을 공유합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> createSolution(
            @Valid @RequestBody CreateSolutionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            SolutionDetailResponse result = solutionService.create(request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 정답 공유 수정.
     *
     * @param id          솔루션 ID
     * @param request     수정 요청 DTO
     * @param userDetails 로그인 사용자 정보
     * @return 성공 메시지
     */
    @Operation(summary = "정답 공유 수정", description = "자신의 정답 공유 글을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSolution(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSolutionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            solutionService.update(id, request, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 정답 공유 삭제.
     *
     * @param id          솔루션 ID
     * @param userDetails 로그인 사용자 정보
     * @return 성공 메시지
     */
    @Operation(summary = "정답 공유 삭제", description = "자신의 정답 공유 글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSolution(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            solutionService.delete(id, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 좋아요 토글.
     *
     * @param id          솔루션 ID
     * @param userDetails 로그인 사용자 정보
     * @return 성공 메시지
     */
    @Operation(summary = "좋아요 토글", description = "정답 공유 글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            solutionService.toggleLike(id, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}

