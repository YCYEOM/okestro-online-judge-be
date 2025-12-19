package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.PageRequestDto;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.OrganizationRankingResponse;
import com.okestro.okestroonlinejudge.dto.response.UserRankingResponse;
import com.okestro.okestroonlinejudge.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 랭킹 관련 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Ranking", description = "랭킹 관련 API")
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 전체 사용자 랭킹 조회.
     */
    @Operation(summary = "전체 사용자 랭킹 조회", description = "전체 사용자의 랭킹을 조회합니다. 랭킹 포인트 내림차순, 해결 문제 수 내림차순으로 정렬됩니다.")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserRankingResponse>>> getUserRankings(@ModelAttribute PageRequestDto pageRequest) {
        Page<UserRankingResponse> rankings = rankingService.getUserRankings(pageRequest.toPageable());
        return ResponseEntity.ok(ApiResponse.success(rankings));
    }

    /**
     * 전체 조직 랭킹 조회.
     */
    @Operation(summary = "전체 조직 랭킹 조회", description = "전체 조직의 랭킹을 조회합니다. 조직 내 사용자들의 랭킹 포인트 합계 내림차순으로 정렬됩니다.")
    @GetMapping("/organizations")
    public ResponseEntity<ApiResponse<Page<OrganizationRankingResponse>>> getOrganizationRankings(@ModelAttribute PageRequestDto pageRequest) {
        Page<OrganizationRankingResponse> rankings = rankingService.getOrganizationRankings(pageRequest.toPageable());
        return ResponseEntity.ok(ApiResponse.success(rankings));
    }
}



