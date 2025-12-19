package com.okestro.okestroonlinejudge.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

/**
 * TOTP (Time-based One-Time Password) 서비스.
 * Google Authenticator 호환 OTP 생성 및 검증.
 */
@Slf4j
@Service
public class TotpService {

    private static final String ISSUER = "Okestro Online Judge";
    private static final int SECRET_LENGTH = 32;

    @Value("${totp.enabled:true}")
    private boolean totpEnabled;

    @Value("${totp.dev-code:123456}")
    private String devCode;

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;

    public TotpService() {
        this.secretGenerator = new DefaultSecretGenerator(SECRET_LENGTH);
        this.qrGenerator = new ZxingPngQrGenerator();

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    /**
     * 새로운 TOTP Secret 생성.
     *
     * @return Base32 인코딩된 Secret
     */
    public String generateSecret() {
        return secretGenerator.generate();
    }

    /**
     * QR 코드 Data URI 생성 (Base64 이미지).
     *
     * @param email 사용자 이메일
     * @param secret TOTP Secret
     * @return QR 코드 Data URI (img src에 바로 사용 가능)
     */
    public String generateQrCodeDataUri(String email, String secret) {
        QrData qrData = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] imageData = qrGenerator.generate(qrData);
            return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            log.error("QR 코드 생성 실패: email={}", email, e);
            throw new RuntimeException("QR 코드 생성에 실패했습니다.", e);
        }
    }

    /**
     * OTP 코드 검증.
     *
     * @param secret TOTP Secret
     * @param code 사용자 입력 코드 (6자리)
     * @return 유효 여부
     */
    public boolean verifyCode(String secret, String code) {
        if (!totpEnabled) {
            // 개발 모드: 고정 코드 허용
            if (devCode.equals(code)) {
                log.info("[개발 모드] TOTP 검증 성공 (고정 코드 사용)");
                return true;
            }
        }

        boolean valid = codeVerifier.isValidCode(secret, code);
        log.info("TOTP 검증 결과: valid={}", valid);
        return valid;
    }

    /**
     * OTP URL 생성 (otpauth:// 프로토콜).
     * Google Authenticator 앱에서 직접 추가 시 사용.
     *
     * @param email 사용자 이메일
     * @param secret TOTP Secret
     * @return otpauth:// URL
     */
    public String generateOtpAuthUrl(String email, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                ISSUER.replace(" ", "%20"),
                email,
                secret,
                ISSUER.replace(" ", "%20")
        );
    }
}
