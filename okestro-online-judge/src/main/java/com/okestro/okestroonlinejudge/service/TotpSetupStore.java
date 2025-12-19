package com.okestro.okestroonlinejudge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TOTP 설정 임시 저장소 (회원가입 전).
 * 프로덕션에서는 Redis 사용 권장.
 */
@Slf4j
@Component
public class TotpSetupStore {

    private static final int EXPIRATION_MINUTES = 10;

    private final Map<String, TotpSetup> setupStore = new ConcurrentHashMap<>();

    /**
     * TOTP 설정 정보 저장.
     *
     * @param email 이메일
     * @param secret TOTP Secret
     */
    public void save(String email, String secret) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        setupStore.put(email.toLowerCase(), new TotpSetup(secret, expiresAt, false));
        log.info("TOTP 설정 저장: email={}, expiresAt={}", email, expiresAt);
    }

    /**
     * TOTP Secret 조회.
     *
     * @param email 이메일
     * @return TOTP Secret, 없거나 만료되면 null
     */
    public String getSecret(String email) {
        TotpSetup setup = setupStore.get(email.toLowerCase());
        if (setup == null || setup.isExpired()) {
            return null;
        }
        return setup.secret();
    }

    /**
     * TOTP 검증 완료 상태로 변경.
     *
     * @param email 이메일
     */
    public void markVerified(String email) {
        TotpSetup setup = setupStore.get(email.toLowerCase());
        if (setup != null && !setup.isExpired()) {
            setupStore.put(email.toLowerCase(),
                    new TotpSetup(setup.secret(), setup.expiresAt(), true));
            log.info("TOTP 검증 완료: email={}", email);
        }
    }

    /**
     * TOTP 검증 완료 여부 확인.
     *
     * @param email 이메일
     * @return 검증 완료 여부
     */
    public boolean isVerified(String email) {
        TotpSetup setup = setupStore.get(email.toLowerCase());
        return setup != null && !setup.isExpired() && setup.verified();
    }

    /**
     * TOTP 설정 정보 삭제 (회원가입 완료 후).
     *
     * @param email 이메일
     */
    public void remove(String email) {
        setupStore.remove(email.toLowerCase());
        log.info("TOTP 설정 삭제: email={}", email);
    }

    /**
     * 만료된 설정 정리.
     */
    public void cleanExpired() {
        int before = setupStore.size();
        setupStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removed = before - setupStore.size();
        if (removed > 0) {
            log.info("만료된 TOTP 설정 {} 건 삭제", removed);
        }
    }

    private record TotpSetup(String secret, LocalDateTime expiresAt, boolean verified) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
