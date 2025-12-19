package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문제 검증 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {

    /**
     * 모든 테스트케이스 통과 여부
     */
    private boolean success;

    /**
     * 각 테스트케이스 결과
     */
    private List<TestCaseResult> results;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseResult {
        /**
         * 테스트케이스 인덱스 (0부터)
         */
        private int index;

        /**
         * 통과 여부
         */
        private boolean passed;

        /**
         * 실제 출력
         */
        private String actualOutput;

        /**
         * 에러 메시지
         */
        private String error;

        /**
         * 실행 시간 (ms)
         */
        private Double time;
    }
}
