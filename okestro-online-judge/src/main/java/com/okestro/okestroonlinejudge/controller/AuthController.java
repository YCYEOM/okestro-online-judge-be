package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.SignUpRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.TokenResponse;
import com.okestro.okestroonlinejudge.dto.response.UserResponse;
import com.okestro.okestroonlinejudge.security.CustomUserDetails;
import com.okestro.okestroonlinejudge.service.AuthService;
import com.okestro.okestroonlinejudge.service.EmailService;
import com.okestro.okestroonlinejudge.service.TotpService;
import com.okestro.okestroonlinejudge.service.TotpSetupStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 관련 API 컨트롤러.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인, 회원가입, 토큰 관리 API")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final TotpService totpService;
    private final TotpSetupStore totpSetupStore;

    /**
     * OAuth2 토큰 발급 (로그인/갱신).
     * FE에서 application/x-www-form-urlencoded 형식으로 요청.
     */
    @PostMapping(value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "토큰 발급", description = "로그인 또는 토큰 갱신")
    public ResponseEntity<TokenResponse> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "refresh_token", required = false) String refreshToken) {

        // FE에서 email로 보내면 username으로 사용
        String loginId = username != null ? username : email;
        log.info("토큰 요청: grant_type={}, loginId={}", grantType, loginId);

        TokenResponse response;

        if ("password".equals(grantType)) {
            // 로그인
            response = authService.login(loginId, password);
        } else if ("refresh_token".equals(grantType)) {
            // 토큰 갱신
            response = authService.refreshToken(refreshToken);
        } else {
            throw new IllegalArgumentException("지원하지 않는 grant_type입니다: " + grantType);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입.
     */
    @PostMapping("/u/auth/sign-up")
    @Operation(summary = "회원가입", description = "새 사용자 등록")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());
        UserResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, 201));
    }

    /**
     * 닉네임 중복 확인.
     */
    @GetMapping("/u/auth/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "사용 가능한 닉네임인지 확인")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNickname(
            @RequestParam("nickname") String nickname) {
        boolean available = authService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }

    /**
     * 이메일 중복 확인.
     */
    @GetMapping("/u/auth/check-email")
    @Operation(summary = "이메일 중복 확인", description = "사용 가능한 이메일인지 확인")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam("email") String email) {
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }

    /**
     * 현재 로그인한 사용자 정보 조회.
     */
    @GetMapping("/u/users/me")
    @Operation(summary = "현재 사용자 정보", description = "로그인한 사용자의 정보 조회")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다.", 401));
        }
        UserResponse response = authService.getCurrentUser(userDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 현재 로그인한 사용자 상세 정보 조회.
     */
    @GetMapping("/u/users/me/detail")
    @Operation(summary = "현재 사용자 상세 정보", description = "로그인한 사용자의 상세 정보 조회")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다.", 401));
        }
        UserResponse response = authService.getCurrentUser(userDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이메일 인증 코드 API 제거됨 - TOTP 방식으로 대체
    // POST /u/auth/send-code → POST /u/auth/totp/setup
    // POST /u/auth/verify-code → POST /u/auth/totp/verify

    /**
     * 임시 비밀번호 발송 (비밀번호 찾기) - 레거시, TOTP 방식 권장.
     */
    @PostMapping("/u/auth/temp-password")
    @Operation(summary = "임시 비밀번호 발송", description = "비밀번호 찾기용 임시 비밀번호 발송 (레거시)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendTempPassword(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("임시 비밀번호 발송 요청: {}", email);

        // 사용자 존재 확인
        if (authService.isEmailAvailable(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("등록되지 않은 이메일입니다.", 404));
        }

        String tempPassword = emailService.sendTempPassword(email);

        if (tempPassword != null) {
            // 임시 비밀번호로 사용자 비밀번호 변경
            authService.updatePassword(email, tempPassword);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "sent", true,
                    "message", "임시 비밀번호가 발송되었습니다."
            )));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("임시 비밀번호 발송에 실패했습니다.", 500));
        }
    }

    // ==================== TOTP 기반 비밀번호 재설정 API ====================

    /**
     * 비밀번호 재설정 - 사용자 확인 (이메일로 TOTP 등록 여부 확인).
     */
    @PostMapping("/u/auth/password-reset/check")
    @Operation(summary = "비밀번호 재설정 - 사용자 확인", description = "이메일로 사용자 존재 및 TOTP 등록 여부 확인")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPasswordResetUser(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("비밀번호 재설정 사용자 확인 요청: email={}", email);

        // 사용자 존재 확인
        if (authService.isEmailAvailable(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("등록되지 않은 이메일입니다.", 404));
        }

        // TOTP 등록 여부 확인
        boolean hasTotpRegistered = authService.hasTotpSecret(email);

        if (!hasTotpRegistered) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("TOTP가 등록되지 않은 사용자입니다. 관리자에게 문의하세요.", 400));
        }

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "exists", true,
                "hasTotpRegistered", true,
                "message", "OTP 인증 후 비밀번호를 재설정할 수 있습니다."
        )));
    }

    /**
     * 비밀번호 재설정 - TOTP 인증 및 비밀번호 변경.
     */
    @PostMapping("/u/auth/password-reset/verify")
    @Operation(summary = "비밀번호 재설정 - TOTP 인증 및 변경", description = "TOTP 코드 검증 후 새 비밀번호로 변경")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPasswordWithTotp(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        log.info("비밀번호 재설정 요청: email={}", email);

        // 필수 필드 검증
        if (email == null || code == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("이메일, OTP 코드, 새 비밀번호가 필요합니다.", 400));
        }

        // 비밀번호 길이 검증
        if (newPassword.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("비밀번호는 8자 이상이어야 합니다.", 400));
        }

        // 사용자 존재 확인
        if (authService.isEmailAvailable(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("등록되지 않은 이메일입니다.", 404));
        }

        // 사용자의 TOTP Secret으로 코드 검증
        boolean verified = authService.verifyTotpForPasswordReset(email, code);

        if (!verified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("OTP 코드가 일치하지 않습니다.", 401));
        }

        // 비밀번호 변경
        authService.updatePassword(email, newPassword);

        log.info("비밀번호 재설정 완료: email={}", email);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "success", true,
                "message", "비밀번호가 성공적으로 변경되었습니다."
        )));
    }

    /**
     * 약관 조회.
     */
    @GetMapping("/auth/term")
    @Operation(summary = "약관 조회", description = "이용약관 조회")
    public ResponseEntity<ApiResponse<Map<String, String>>> getTerm(
            @RequestParam(value = "type", defaultValue = "service") String type) {
        String content = switch (type) {
            case "privacy" -> "개인정보 처리방침 내용...";
            default -> "서비스 이용약관 내용...";
        };
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "type", type,
                "content", content
        )));
    }

    // ==================== TOTP 인증 API ====================

    /**
     * TOTP 설정 시작 (QR 코드 생성).
     * 회원가입 시 이메일 입력 후 호출.
     */
    @PostMapping("/u/auth/totp/setup")
    @Operation(summary = "TOTP 설정", description = "Google Authenticator 등록용 QR 코드 생성")
    public ResponseEntity<ApiResponse<Map<String, String>>> setupTotp(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("TOTP 설정 요청: email={}", email);

        // 이메일 중복 확인
        if (!authService.isEmailAvailable(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("이미 사용 중인 이메일입니다.", 409));
        }

        // Secret 생성 및 저장
        String secret = totpService.generateSecret();
        totpSetupStore.save(email, secret);

        // QR 코드 Data URI 생성
        String qrCodeDataUri = totpService.generateQrCodeDataUri(email, secret);
        String otpAuthUrl = totpService.generateOtpAuthUrl(email, secret);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "qrCode", qrCodeDataUri,
                "otpAuthUrl", otpAuthUrl,
                "secret", secret  // 수동 입력용
        )));
    }

    /**
     * TOTP 코드 검증.
     * QR 코드 스캔 후 OTP 입력하여 검증.
     */
    @PostMapping("/u/auth/totp/verify")
    @Operation(summary = "TOTP 검증", description = "Google Authenticator OTP 코드 검증")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyTotp(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        log.info("TOTP 검증 요청: email={}", email);

        // 임시 저장된 Secret 조회
        String secret = totpSetupStore.getSecret(email);
        if (secret == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("TOTP 설정이 만료되었습니다. 다시 시도해주세요.", 400));
        }

        // OTP 검증
        boolean verified = totpService.verifyCode(secret, code);

        if (verified) {
            totpSetupStore.markVerified(email);
            return ResponseEntity.ok(ApiResponse.success(Map.of("verified", true)));
        } else {
            return ResponseEntity.ok(ApiResponse.success(Map.of("verified", false)));
        }
    }

    /**
     * TOTP 검증 상태 확인.
     */
    @GetMapping("/u/auth/totp/status")
    @Operation(summary = "TOTP 검증 상태", description = "TOTP 검증 완료 여부 확인")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotpStatus(
            @RequestParam("email") String email) {
        boolean verified = totpSetupStore.isVerified(email);
        String secret = totpSetupStore.getSecret(email);
        boolean exists = secret != null;

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "exists", exists,
                "verified", verified
        )));
    }
}
