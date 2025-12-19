package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 테스트케이스의 출력을 검증하는 유틸리티 클래스.
 *
 * @author Cascade
 * @since 2025-12-08
 */
@Slf4j
@UtilityClass
public class OutputValidator {

    // 기본 허용 오차 (부동소수점 비교 시)
    private static final double DEFAULT_EPSILON = 1e-9;

    /**
     * 출력을 검증합니다.
     *
     * @param output         실제 출력
     * @param expectedOutput 예상 출력
     * @param options        검증 옵션
     * @return 검증 결과
     */
    public static ValidationResult validate(String output, String expectedOutput, ValidationOptions options) {
        if (output == null || expectedOutput == null) {
            return new ValidationResult(false, "출력이 null입니다.");
        }

        // 공백/개행 무시 옵션 적용
        if (options.isIgnoreWhitespace()) {
            output = normalizeWhitespace(output);
            expectedOutput = normalizeWhitespace(expectedOutput);
        }

        // 정확한 일치
        if (options.isExactMatch()) {
            boolean isEqual = output.equals(expectedOutput);
            return new ValidationResult(isEqual, isEqual ? null : "출력이 일치하지 않습니다.");
        }

        // 줄 단위 비교 (부동소수점 허용 오차 적용)
        String[] outputLines = output.split("\\r?\\n");
        String[] expectedLines = expectedOutput.split("\\r?\\n");

        if (outputLines.length != expectedLines.length) {
            return new ValidationResult(false, 
                String.format("출력 줄 수가 일치하지 않습니다. (기대: %d, 실제: %d)", 
                    expectedLines.length, outputLines.length));
        }

        for (int i = 0; i < outputLines.length; i++) {
            String line = outputLines[i].trim();
            String expectedLine = expectedLines[i].trim();

            // 줄 단위 정확 일치
            if (line.equals(expectedLine)) {
                continue;
            }

            // 부동소수점 비교 시도
            if (options.isAllowFloatingPointError()) {
                try {
                    if (isFloatingPointEqual(line, expectedLine, options.getEpsilon())) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    // 부동소수점 파싱 실패 시 정확한 일치로 처리
                }
            }

            return new ValidationResult(false, 
                String.format("출력이 일치하지 않습니다. (줄 %d: 기대: '%s', 실제: '%s')", 
                    i + 1, expectedLine, line));
        }

        return new ValidationResult(true, null);
    }

    /**
     * 부동소수점 문자열을 비교합니다.
     */
    private static boolean isFloatingPointEqual(String s1, String s2, double epsilon) {
        String[] tokens1 = s1.split("\\s+");
        String[] tokens2 = s2.split("\\s+");

        if (tokens1.length != tokens2.length) {
            return false;
        }

        for (int i = 0; i < tokens1.length; i++) {
            try {
                double d1 = Double.parseDouble(tokens1[i]);
                double d2 = Double.parseDouble(tokens2[i]);
                
                if (Math.abs(d1 - d2) > epsilon) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 토큰은 문자열로 비교
                if (!tokens1[i].equals(tokens2[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 공백을 정규화합니다.
     */
    private static String normalizeWhitespace(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("\\s+", " ").trim();
    }

    /**
     * 검증 결과를 나타내는 클래스.
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
    }

    /**
     * 검증 옵션을 나타내는 클래스.
     */
    @lombok.Getter
    @lombok.Builder
    public static class ValidationOptions {
        /** 정확한 일치 여부 (기본: true) */
        @lombok.Builder.Default
        private boolean exactMatch = true;

        /** 공백/개행 무시 여부 (기본: false) */
        @lombok.Builder.Default
        private boolean ignoreWhitespace = false;

        /** 부동소수점 오차 허용 여부 (기본: false) */
        @lombok.Builder.Default
        private boolean allowFloatingPointError = false;

        /** 부동소수점 비교 시 허용 오차 (기본: 1e-9) */
        @lombok.Builder.Default
        private double epsilon = DEFAULT_EPSILON;
    }
}
