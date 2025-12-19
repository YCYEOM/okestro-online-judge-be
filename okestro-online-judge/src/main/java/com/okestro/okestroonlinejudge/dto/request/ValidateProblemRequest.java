package com.okestro.okestroonlinejudge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문제 검증 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateProblemRequest {

    /**
     * 정답 코드
     */
    private String solutionCode;

    /**
     * 언어 (python, java, cpp, c, javascript)
     */
    private String language;

    /**
     * 테스트케이스 목록
     */
    private List<TestCaseInput> testCases;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseInput {
        private String input;
        private String expectedOutput;
    }
}
