package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.UpdatePrivacyRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateProfileRequest;
import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 사용자 정보 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "User", description = "사용자 정보 관련 API")
@RestController
@RequestMapping("/oj/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회.
     */
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            UserProfileResponse result = userService.getProfileByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 프로필 수정 (이름 제외).
     */
    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 프로필 정보를 수정합니다. (이름은 변경 불가)")
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UpdateProfileRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            UserProfileResponse result = userService.updateProfile(username, request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 닉네임 변경 (닉네임 변경권 필요).
     */
    @Operation(summary = "닉네임 변경", description = "닉네임 변경권을 사용하여 닉네임을 변경합니다.")
    @PutMapping("/me/nickname")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            String username = userDetails.getUsername();
            String newNickname = request.get("nickname");

            if (newNickname == null || newNickname.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.badRequest("닉네임을 입력해주세요."));
            }

            if (newNickname.length() < 2 || newNickname.length() > 19) {
                return ResponseEntity.badRequest().body(ApiResponse.badRequest("닉네임은 2~19자여야 합니다."));
            }

            UserProfileResponse result = userService.updateNickname(username, newNickname);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 프로필 이미지 업로드.
     */
    @Operation(summary = "내 프로필 이미지 업로드", description = "프로필 이미지를 업로드합니다.")
    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "이미지 파일") @RequestPart("file") MultipartFile file
    ) {
        try {
            String username = userDetails.getUsername();
            String imageUrl = userService.uploadProfileImage(username, file);
            return ResponseEntity.ok(ApiResponse.success(imageUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 프로필 이미지 삭제.
     */
    @Operation(summary = "내 프로필 이미지 삭제", description = "프로필 이미지를 삭제(초기화)합니다.")
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            userService.deleteProfileImage(username);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 회원 탈퇴.
     */
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다 (계정 익명화).")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            userService.withdrawUser(username);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 통계 조회.
     */
    @Operation(summary = "내 통계 조회", description = "로그인한 사용자의 통계 정보를 조회합니다.")
    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getMyStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            UserStatsResponse result = userService.getStatsByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 스트릭 조회.
     */
    @Operation(summary = "내 스트릭 조회", description = "로그인한 사용자의 스트릭(잔디) 정보를 조회합니다.")
    @GetMapping("/me/streak")
    public ResponseEntity<ApiResponse<UserStreakResponse>> getMyStreak(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year
    ) {
        try {
            String username = userDetails.getUsername();
            UserStreakResponse result = userService.getUserStreak(username, year);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내가 시도한 문제 목록 조회 (solved, failed 포함).
     * FE 호환성을 위해 /me/solved 경로도 지원.
     */
    @Operation(summary = "내가 시도한 문제 목록", description = "로그인한 사용자가 시도한 문제 목록을 조회합니다.")
    @GetMapping({"/me/problems", "/me/solved"})
    public ResponseEntity<ApiResponse<Page<AttemptedProblemResponse>>> getMyAttemptedProblems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            String username = userDetails.getUsername();
            Page<AttemptedProblemResponse> result = userService.getAttemptedProblemsByUsername(username, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 제출 이력 조회.
     */
    @Operation(summary = "내 제출 이력 조회", description = "로그인한 사용자의 제출 이력을 조회합니다.")
    @GetMapping("/me/submissions")
    public ResponseEntity<ApiResponse<Page<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            String username = userDetails.getUsername();
            Page<SubmissionResponse> result = userService.getSubmissionsByUsername(username, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 프로필 공개 설정 API ====================

    /**
     * 내 프로필 공개 설정 조회.
     */
    @Operation(summary = "내 프로필 공개 설정 조회", description = "프로필 공개 설정을 조회합니다.")
    @GetMapping("/me/privacy")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> getMyPrivacySettings(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String username = userDetails.getUsername();
            PrivacySettingsResponse result = userService.getPrivacySettings(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 내 프로필 공개 설정 변경.
     */
    @Operation(summary = "내 프로필 공개 설정 변경", description = "프로필 공개 설정을 변경합니다.")
    @PutMapping("/me/privacy")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> updateMyPrivacySettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdatePrivacyRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            PrivacySettingsResponse result = userService.updatePrivacySettings(
                    username,
                    request.getProfilePublic(),
                    request.getStatsPublic(),
                    request.getSolvedProblemsPublic(),
                    request.getActivityPublic()
            );
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 다른 사용자 프로필 조회 API ====================

    /**
     * 다른 사용자 프로필 조회 (공개 정보만).
     */
    @Operation(summary = "다른 사용자 프로필 조회", description = "다른 사용자의 공개 프로필을 조회합니다. 젬(포인트)은 표시되지 않습니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<PublicProfileResponse>> getUserProfile(
            @PathVariable Long userId
    ) {
        try {
            PublicProfileResponse result = userService.getPublicProfile(userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * username으로 다른 사용자 프로필 조회.
     */
    @Operation(summary = "username으로 다른 사용자 프로필 조회", description = "username으로 다른 사용자의 공개 프로필을 조회합니다.")
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<PublicProfileResponse>> getUserProfileByUsername(
            @PathVariable String username
    ) {
        try {
            PublicProfileResponse result = userService.getPublicProfileByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 다른 사용자 통계 조회 (공개 설정에 따름).
     */
    @Operation(summary = "다른 사용자 통계 조회", description = "다른 사용자의 통계를 조회합니다. 공개 설정에 따라 제한될 수 있습니다.")
    @GetMapping("/{userId}/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(
            @PathVariable Long userId
    ) {
        try {
            UserStatsResponse result = userService.getPublicStats(userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 다른 사용자 풀이 기록 조회 (공개 설정에 따름).
     */
    @Operation(summary = "다른 사용자 풀이 기록 조회", description = "다른 사용자의 풀이 기록을 조회합니다. 비공개 시 빈 결과가 반환됩니다.")
    @GetMapping("/{userId}/problems")
    public ResponseEntity<ApiResponse<Page<AttemptedProblemResponse>>> getUserAttemptedProblems(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Page<AttemptedProblemResponse> result = userService.getPublicAttemptedProblems(userId, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
