package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.client.Judge0Client;
import com.okestro.okestroonlinejudge.dto.request.ValidateProblemRequest;
import com.okestro.okestroonlinejudge.dto.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 문제 검증 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final Judge0Client judge0Client;

    /**
     * 정답 코드로 모든 테스트케이스를 실행하여 검증한다.
     *
     * @param request 검증 요청
     * @return 검증 결과
     */
    public ValidationResponse validateProblem(ValidateProblemRequest request) {
        List<ValidationResponse.TestCaseResult> results = new ArrayList<>();
        boolean allPassed = true;

        for (int i = 0; i < request.getTestCases().size(); i++) {
            ValidateProblemRequest.TestCaseInput tc = request.getTestCases().get(i);

            Judge0Client.JudgeResult judgeResult = judge0Client.judge(
                    request.getSolutionCode(),
                    request.getLanguage(),
                    tc.getInput(),
                    tc.getExpectedOutput()
            );

            boolean passed = judgeResult.isAccepted();
            if (!passed) {
                allPassed = false;
            }

            String error = null;
            if (!passed) {
                if (judgeResult.getErrorMessage() != null) {
                    error = judgeResult.getErrorMessage();
                } else if (judgeResult.getCompileOutput() != null && !judgeResult.getCompileOutput().isEmpty()) {
                    error = "Compile Error: " + judgeResult.getCompileOutput();
                } else if (judgeResult.getStderr() != null && !judgeResult.getStderr().isEmpty()) {
                    error = "Runtime Error: " + judgeResult.getStderr();
                } else if (judgeResult.getStatusDescription() != null) {
                    error = judgeResult.getStatusDescription();
                } else {
                    error = "Wrong Answer";
                }
            }

            // 실행 시간을 ms로 변환 (Judge0는 초 단위로 반환)
            Double timeMs = null;
            if (judgeResult.getExecutionTime() != null) {
                timeMs = judgeResult.getExecutionTime() * 1000;
            }

            results.add(ValidationResponse.TestCaseResult.builder()
                    .index(i)
                    .passed(passed)
                    .actualOutput(judgeResult.getStdout())
                    .error(error)
                    .time(timeMs)
                    .build());

            log.info("TestCase {} validation: passed={}, time={}ms",
                    i + 1, passed, timeMs != null ? String.format("%.2f", timeMs) : "N/A");
        }

        return ValidationResponse.builder()
                .success(allPassed)
                .results(results)
                .build();
    }
}
