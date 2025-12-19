package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.CreateTestCaseRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.TestCaseResponse;
import com.okestro.okestroonlinejudge.service.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 테스트케이스 관리 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "TestCase", description = "테스트케이스 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    /**
     * 문제에 테스트케이스 추가
     */
    @Operation(summary = "테스트케이스 생성", description = "문제에 테스트케이스를 추가합니다.")
    @PostMapping("/problems/{problemId}/testcases")
    public ResponseEntity<ApiResponse<TestCaseResponse>> createTestCase(
            @PathVariable Long problemId,
            @RequestBody CreateTestCaseRequest request
    ) {
        try {
            TestCaseResponse created = testCaseService.createTestCase(problemId, request);
            return ResponseEntity.ok(ApiResponse.success(created, 201));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 문제의 테스트케이스 목록 조회
     */
    @Operation(summary = "테스트케이스 목록 조회", description = "문제의 테스트케이스 목록을 조회합니다.")
    @GetMapping("/problems/{problemId}/testcases")
    public ResponseEntity<ApiResponse<List<TestCaseResponse>>> getTestCases(
            @PathVariable Long problemId
    ) {
        try {
            List<TestCaseResponse> testCases = testCaseService.getTestCases(problemId);
            return ResponseEntity.ok(ApiResponse.success(testCases));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 테스트케이스 삭제
     */
    @Operation(summary = "테스트케이스 삭제", description = "테스트케이스를 삭제합니다.")
    @DeleteMapping("/testcases/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable Long id) {
        try {
            testCaseService.deleteTestCase(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }
}
