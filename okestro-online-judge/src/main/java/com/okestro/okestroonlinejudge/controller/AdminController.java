package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.UpdateUserRoleRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.UserResponse;
import com.okestro.okestroonlinejudge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    /**
     * 전체 사용자 목록 조회.
     */
    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 페이징하여 조회합니다. (ADMIN 전용)")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 특정 사용자 조회.
     */
    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자 정보를 조회합니다. (ADMIN 전용)")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 사용자 권한 변경.
     */
    @Operation(summary = "사용자 권한 변경", description = "사용자의 권한(USER/ADMIN)을 변경합니다. (ADMIN 전용)")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        try {
            UserResponse user = userService.updateUserRole(userId, request.getRole());
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
