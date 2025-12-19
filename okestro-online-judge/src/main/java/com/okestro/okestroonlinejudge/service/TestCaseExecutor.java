package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.client.Judge0Client;
import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import com.okestro.okestroonlinejudge.service.SubmissionService.TestCaseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 테스트케이스 실행을 담당하는 서비스.
 *
 * @author Cascade
 * @since 2025-12-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseExecutor {

    private final Judge0Client judge0Client;
    private final StorageService storageService;

    @Value("${judge0.max-concurrent-executions:5}")
    private int maxConcurrentExecutions;

    @Value("${judge0.timeout-multiplier:2.0}")
    private double timeoutMultiplier;

    @Value("${judge0.memory-limit:262144}")
    private int memoryLimitKb;

    @Value("${judge0.cpu-time-limit:5}")
    private int cpuTimeLimitSec;

    @Value("${minio.testcase-bucket-name:okestro-testcases}")
    private String testcaseBucketName;

    /**
     * 테스트케이스들을 실행하고 결과를 반환합니다.
     *
     * @param sourceCode 소스 코드
     * @param language   프로그래밍 언어
     * @param testCases  테스트케이스 목록
     * @return 테스트케이스 실행 결과 목록
     */
    public List<TestCaseResult> executeTestCases(String sourceCode, String language, List<TestCaseEntity> testCases) {
        List<TestCaseResult> results = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(maxConcurrentExecutions);
        List<Future<TestCaseResult>> futures = new ArrayList<>();

        try {
            // 모든 테스트케이스를 비동기로 실행
            for (int i = 0; i < testCases.size(); i++) {
                final int index = i;
                Future<TestCaseResult> future = executor.submit(() -> 
                    executeSingleTestCase(sourceCode, language, testCases.get(index), index + 1)
                );
                futures.add(future);
            }

            // 결과 수집
            for (Future<TestCaseResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("테스트케이스 실행 중단됨", e);
                    // 중단된 테스트케이스에 대한 오류 결과 추가
                    results.add(createErrorResult("테스트케이스 실행이 중단되었습니다."));
                } catch (ExecutionException e) {
                    log.error("테스트케이스 실행 중 오류 발생", e);
                    results.add(createErrorResult("테스트케이스 실행 중 오류가 발생했습니다: " + e.getCause().getMessage()));
                }
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        return results;
    }

    /**
     * 단일 테스트케이스를 실행합니다.
     *
     * @param sourceCode 소스 코드
     * @param language   프로그래밍 언어
     * @param testCase   테스트케이스 엔티티
     * @param testCaseNumber 테스트케이스 번호
     * @return 테스트케이스 실행 결과
     */
    private TestCaseResult executeSingleTestCase(String sourceCode, String language,
                                               TestCaseEntity testCase, int testCaseNumber) {
        try {
            String input = storageService.readString(testCase.getInputPath(), testcaseBucketName);
            String expectedOutput = storageService.readString(testCase.getOutputPath(), testcaseBucketName);
            
            log.debug("테스트케이스 {} - input 길이: {}, output 길이: {}", 
                    testCaseNumber, input.length(), expectedOutput.length());

            // Judge0로 코드 실행
            Judge0Client.JudgeResult judgeResult = judge0Client.judge(
                    sourceCode,
                    language,
                    input,
                    expectedOutput
            );

            // 결과 반환
            return TestCaseResult.builder()
                    .testCaseId(testCase.getId())
                    .testCaseNumber(testCaseNumber)
                    .result(judgeResult.getResult())
                    .executionTime(judgeResult.getExecutionTime())
                    .memoryUsage(judgeResult.getMemoryUsage())
                    .stdout(judgeResult.getStdout())
                    .stderr(judgeResult.getStderr())
                    .input(input)
                    .expectedOutput(expectedOutput)
                    .actualOutput(judgeResult.getStdout())
                    .errorMessage(judgeResult.getStderr())
                    .isSample(testCase.getIsSample())
                    .build();
        } catch (Exception e) {
            log.error("테스트케이스 {} 실행 중 오류 발생: {}", testCaseNumber, e.getMessage(), e);
            return createErrorResult("테스트케이스 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 오류 결과를 생성합니다.
     *
     * @param errorMessage 오류 메시지
     * @return 오류가 포함된 테스트케이스 결과
     */
    private TestCaseResult createErrorResult(String errorMessage) {
        return TestCaseResult.builder()
                .testCaseId(null)
                .testCaseNumber(0)
                .result(SubmissionResult.RUNTIME_ERROR)
                .executionTime(0.0)
                .memoryUsage(0)
                .stdout("")
                .stderr(errorMessage)
                .input("")
                .expectedOutput("")
                .actualOutput("")
                .errorMessage(errorMessage)
                .isSample(false)
                .build();
    }
}
