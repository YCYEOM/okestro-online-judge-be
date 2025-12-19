package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.CreateProblemRequest;
import com.okestro.okestroonlinejudge.dto.request.SearchProblemRequest;
import com.okestro.okestroonlinejudge.dto.request.ValidateProblemRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.ProblemDetailResponse;
import com.okestro.okestroonlinejudge.dto.response.ValidationResponse;
import com.okestro.okestroonlinejudge.security.CustomUserDetails;
import com.okestro.okestroonlinejudge.service.ProblemService;
import com.okestro.okestroonlinejudge.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 문제 관리 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Problem", description = "문제 관련 API")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final ValidationService validationService;

    /**
     * 문제 목록 조회 (검색 및 페이징).
     */
    @Operation(summary = "문제 목록 조회", description = "문제 목록을 검색 조건과 함께 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProblemDetailResponse>>> getProblems(
            @Parameter(description = "검색 요청 파라미터") @ModelAttribute SearchProblemRequest request
    ) {
        Page<ProblemDetailResponse> problems = problemService.getProblems(request);
        return ResponseEntity.ok(ApiResponse.success(problems));
    }

    /**
     * 문제 상세 조회
     */
    @Operation(summary = "문제 상세 조회", description = "ID로 문제 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProblemDetailResponse>> getProblem(@PathVariable Long id) {
        try {
            ProblemDetailResponse problem = problemService.getProblem(id);
            return ResponseEntity.ok(ApiResponse.success(problem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 문제 생성
     */
    @Operation(summary = "문제 생성", description = "새로운 문제를 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<ProblemDetailResponse>> createProblem(
            @RequestBody CreateProblemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            String username = userDetails != null ? userDetails.getUsername() : null;
            ProblemDetailResponse created = problemService.createProblem(request, username);
            return ResponseEntity.ok(ApiResponse.success(created, 201));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 문제 수정
     */
    @Operation(summary = "문제 수정", description = "기존 문제를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProblemDetailResponse>> updateProblem(
            @PathVariable Long id,
            @RequestBody CreateProblemRequest request
    ) {
        try {
            ProblemDetailResponse updated = problemService.updateProblem(id, request);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 문제 삭제 (ADMIN 전용)
     */
    @Operation(summary = "문제 삭제", description = "문제를 삭제합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProblem(@PathVariable Long id) {
        try {
            problemService.deleteProblem(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 문제 검증 (정답 코드로 테스트케이스 실행)
     */
    @Operation(summary = "문제 검증", description = "정답 코드로 모든 테스트케이스를 실행하여 검증합니다.")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidationResponse>> validateProblem(
            @RequestBody ValidateProblemRequest request
    ) {
        try {
            ValidationResponse result = validationService.validateProblem(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.internalError("검증 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 추천 문제 목록 조회 (홈 페이지용)
     */
    @Operation(summary = "추천 문제 목록 조회", description = "홈 페이지에 표시할 추천 문제 목록을 조회합니다.")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProblemDetailResponse>>> getFeaturedProblems(
            @Parameter(description = "조회할 문제 수") @RequestParam(defaultValue = "3") int limit
    ) {
        try {
            List<ProblemDetailResponse> problems = problemService.getFeaturedProblems(limit);
            return ResponseEntity.ok(ApiResponse.success(problems));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.internalError("추천 문제 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
