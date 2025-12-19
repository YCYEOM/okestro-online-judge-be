package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 출석 체크 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Attendance", description = "출석 체크 관련 API")
@RestController
@RequestMapping("/oj")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 출석 체크 (1일 1회).
     */
    @Operation(summary = "출석 체크", description = "오늘의 출석 체크를 수행합니다. 1일 1회만 가능합니다.")
    @PostMapping("/attendance/check")
    public ResponseEntity<ApiResponse<AttendanceCheckResponse>> checkAttendance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            AttendanceCheckResponse result = attendanceService.checkAttendanceByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 오늘 출석 여부 확인.
     */
    @Operation(summary = "오늘 출석 여부 확인", description = "오늘 출석 체크를 했는지 확인합니다.")
    @GetMapping("/attendance/today")
    public ResponseEntity<ApiResponse<TodayAttendanceResponse>> getTodayAttendance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            TodayAttendanceResponse result = attendanceService.getTodayAttendanceByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 월간 출석 현황 조회.
     */
    @Operation(summary = "월간 출석 현황 조회", description = "특정 년월의 출석 현황을 조회합니다.")
    @GetMapping("/attendance/monthly")
    public ResponseEntity<ApiResponse<MonthlyAttendanceResponse>> getMonthlyAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ) {
        try {
            String username = userDetails.getUsername();
            MonthlyAttendanceResponse result = attendanceService.getMonthlyAttendanceByUsername(username, year, month);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 포인트 정보 조회.
     */
    @Operation(summary = "내 포인트 정보 조회", description = "현재 보유 포인트와 적립/사용 내역을 조회합니다.")
    @GetMapping("/points/me")
    public ResponseEntity<ApiResponse<PointInfoResponse>> getMyPoints(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            PointInfoResponse result = attendanceService.getPointInfoByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 포인트 이력 조회.
     */
    @Operation(summary = "포인트 이력 조회", description = "포인트 적립/사용 이력을 페이징하여 조회합니다.")
    @GetMapping("/points/history")
    public ResponseEntity<ApiResponse<Page<PointHistoryResponse>>> getPointHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            String username = userDetails.getUsername();
            Page<PointHistoryResponse> result = attendanceService.getPointHistoryByUsername(username, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
