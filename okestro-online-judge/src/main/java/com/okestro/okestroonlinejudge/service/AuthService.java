package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.Role;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import com.okestro.okestroonlinejudge.dto.request.SignUpRequest;
import com.okestro.okestroonlinejudge.dto.response.TokenResponse;
import com.okestro.okestroonlinejudge.dto.response.UserResponse;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import com.okestro.okestroonlinejudge.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final TotpSetupStore totpSetupStore;
    private final TotpService totpService;

    /**
     * 로그인 (비밀번호 인증).
     */
    public TokenResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        log.info("로그인 성공: {}", username);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .build();
    }

    /**
     * 토큰 갱신.
     */
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);

        // 사용자 존재 확인
        userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(username);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        log.info("토큰 갱신 성공: {}", username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .build();
    }

    /**
     * 회원가입.
     * TOTP 인증이 완료된 사용자만 회원가입 가능.
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // TOTP 검증 완료 확인
        if (!totpSetupStore.isVerified(request.getEmail())) {
            throw new IllegalArgumentException("TOTP 인증이 완료되지 않았습니다.");
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자명 중복 확인
        if (userRepository.findByUsername(request.getUserName()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // TOTP Secret 가져오기
        String totpSecret = totpSetupStore.getSecret(request.getEmail());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성 (TOTP Secret 포함)
        UserEntity user = new UserEntity(
                request.getUserName(),
                request.getNickname(),
                encodedPassword,
                request.getEmail(),
                Role.USER,
                null, // organization
                null, // tier
                totpSecret // TOTP Secret
        );

        UserEntity savedUser = userRepository.save(user);

        // TOTP 임시 데이터 삭제
        totpSetupStore.remove(request.getEmail());

        log.info("회원가입 성공: {}", request.getEmail());

        return UserResponse.from(savedUser);
    }

    /**
     * 닉네임(사용자명) 중복 확인.
     */
    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    /**
     * 이메일 중복 확인.
     */
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    /**
     * 닉네임 중복 확인.
     */
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * 현재 사용자 정보 조회.
     */
    public UserResponse getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    /**
     * 비밀번호 변경.
     */
    @Transactional
    public void updatePassword(String email, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);

        log.info("비밀번호 변경 완료: {}", email);
    }

    /**
     * 사용자의 TOTP Secret 등록 여부 확인.
     */
    public boolean hasTotpSecret(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getTotpSecret() != null && !user.getTotpSecret().isEmpty())
                .orElse(false);
    }

    /**
     * 비밀번호 재설정을 위한 TOTP 검증.
     * 사용자의 DB에 저장된 TOTP Secret으로 OTP 코드 검증.
     */
    public boolean verifyTotpForPasswordReset(String email, String code) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String totpSecret = user.getTotpSecret();
        if (totpSecret == null || totpSecret.isEmpty()) {
            log.warn("TOTP Secret이 없는 사용자: email={}", email);
            return false;
        }

        boolean verified = totpService.verifyCode(totpSecret, code);
        log.info("비밀번호 재설정 TOTP 검증 결과: email={}, verified={}", email, verified);
        return verified;
    }
}
