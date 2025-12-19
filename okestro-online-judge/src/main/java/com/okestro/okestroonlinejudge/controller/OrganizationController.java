package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.CreateOrganizationRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateOrganizationRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.OrganizationResponse;
import com.okestro.okestroonlinejudge.dto.response.OrganizationTreeResponse;
import com.okestro.okestroonlinejudge.dto.response.UserResponse;
import com.okestro.okestroonlinejudge.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 조직 관리 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * 조직 생성.
     */
    @Operation(summary = "조직 생성", description = "새로운 조직을 생성합니다. parentId가 null이면 최상위 조직으로 생성됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        try {
            OrganizationResponse created = organizationService.createOrganization(request);
            return ResponseEntity.ok(ApiResponse.success(created, 201));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 조직 상세 조회.
     */
    @Operation(summary = "조직 상세 조회", description = "ID로 조직 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long id
    ) {
        try {
            OrganizationResponse organization = organizationService.getOrganization(id);
            return ResponseEntity.ok(ApiResponse.success(organization));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 전체 조직 목록 조회.
     */
    @Operation(summary = "전체 조직 목록 조회", description = "모든 조직 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getAllOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(ApiResponse.success(organizations));
    }

    /**
     * 최상위 조직 목록 조회.
     */
    @Operation(summary = "최상위 조직 목록 조회", description = "최상위(depth=0) 조직 목록을 조회합니다.")
    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getRootOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getRootOrganizations();
        return ResponseEntity.ok(ApiResponse.success(organizations));
    }

    /**
     * 하위 조직 목록 조회.
     */
    @Operation(summary = "하위 조직 목록 조회", description = "특정 조직의 직속 하위 조직 목록을 조회합니다.")
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getChildOrganizations(
            @Parameter(description = "상위 조직 ID") @PathVariable Long id
    ) {
        List<OrganizationResponse> children = organizationService.getChildOrganizations(id);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    /**
     * 조직 트리 구조 조회.
     */
    @Operation(summary = "조직 트리 구조 조회", description = "전체 조직을 계층적 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<OrganizationTreeResponse>>> getOrganizationTree() {
        List<OrganizationTreeResponse> tree = organizationService.getOrganizationTree();
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    /**
     * 특정 depth의 조직 목록 조회.
     */
    @Operation(summary = "특정 depth의 조직 목록 조회", description = "특정 계층 깊이의 조직 목록을 조회합니다.")
    @GetMapping("/depth/{depth}")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getOrganizationsByDepth(
            @Parameter(description = "조직 깊이 (0: 최상위)") @PathVariable Integer depth
    ) {
        List<OrganizationResponse> organizations = organizationService.getOrganizationsByDepth(depth);
        return ResponseEntity.ok(ApiResponse.success(organizations));
    }

    /**
     * 조직 수정.
     */
    @Operation(summary = "조직 수정", description = "조직 정보를 수정합니다. 상위 조직 변경 시 depth가 자동으로 재계산됩니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        try {
            OrganizationResponse updated = organizationService.updateOrganization(id, request);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 조직을 최상위로 이동.
     */
    @Operation(summary = "조직을 최상위로 이동", description = "조직을 최상위(depth=0)로 이동합니다. 하위 조직의 depth도 함께 재계산됩니다.")
    @PatchMapping("/{id}/move-to-root")
    public ResponseEntity<ApiResponse<OrganizationResponse>> moveToRoot(
            @Parameter(description = "조직 ID") @PathVariable Long id
    ) {
        try {
            OrganizationResponse updated = organizationService.moveToRoot(id);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 조직 삭제.
     */
    @Operation(summary = "조직 삭제", description = "조직을 삭제합니다. 하위 조직이 있는 경우 삭제할 수 없습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long id
    ) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 사용자를 조직에 할당.
     */
    @Operation(summary = "사용자를 조직에 할당", description = "특정 사용자를 조직에 할당합니다.")
    @PostMapping("/{organizationId}/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> assignUserToOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long organizationId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        try {
            UserResponse user = organizationService.assignUserToOrganization(userId, organizationId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 사용자의 조직 할당 해제.
     */
    @Operation(summary = "사용자의 조직 할당 해제", description = "사용자를 조직에서 제거합니다.")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> removeUserFromOrganization(
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        try {
            UserResponse user = organizationService.removeUserFromOrganization(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 특정 조직에 속한 사용자 목록 조회.
     */
    @Operation(summary = "조직 소속 사용자 목록 조회", description = "특정 조직에 속한 사용자 목록을 조회합니다.")
    @GetMapping("/{id}/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long id
    ) {
        try {
            List<UserResponse> users = organizationService.getUsersByOrganization(id);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 특정 조직에 속한 사용자 수 조회.
     */
    @Operation(summary = "조직 소속 사용자 수 조회", description = "특정 조직에 속한 사용자 수를 조회합니다.")
    @GetMapping("/{id}/users/count")
    public ResponseEntity<ApiResponse<Long>> getUserCountByOrganization(
            @Parameter(description = "조직 ID") @PathVariable Long id
    ) {
        try {
            long count = organizationService.getUserCountByOrganization(id);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.notFound(e.getMessage()));
        }
    }
}
