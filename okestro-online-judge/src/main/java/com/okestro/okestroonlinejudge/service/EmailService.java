package com.okestro.okestroonlinejudge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * 이메일 발송 서비스 (레거시).
 * TOTP 도입으로 실제 이메일 발송은 사용하지 않음.
 * 임시 비밀번호 생성 기능만 유지.
 */
@Slf4j
@Service
public class EmailService {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 임시 비밀번호 생성 (이메일 발송 없이 로그로만 출력).
     * TOTP 기반 비밀번호 재설정으로 대체됨.
     */
    public String sendTempPassword(String toEmail) {
        String tempPassword = generateTempPassword();
        log.info("[개발 모드] 임시 비밀번호 생성: email={}, password={}", toEmail, tempPassword);
        return tempPassword;
    }

    /**
     * 임시 비밀번호 생성 (8자리 영문+숫자).
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
