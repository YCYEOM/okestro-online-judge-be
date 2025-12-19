package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import com.okestro.okestroonlinejudge.dto.request.CreateTestCaseRequest;
import com.okestro.okestroonlinejudge.dto.response.TestCaseResponse;
import com.okestro.okestroonlinejudge.repository.ProblemRepository;
import com.okestro.okestroonlinejudge.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 테스트케이스 관리 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;
    private final StorageService storageService;

    @Value("${minio.testcase-bucket-name:okestro-testcases}")
    private String bucketName;

    /**
     * 문제에 테스트케이스를 추가한다.
     *
     * @param problemId 문제 ID
     * @param request   테스트케이스 생성 요청
     * @return 생성된 테스트케이스 응답
     */
    @Transactional
    public TestCaseResponse createTestCase(Long problemId, CreateTestCaseRequest request) {
        ProblemEntity problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId));

        // 입력/출력 데이터를 MinIO에 저장
        String inputFileName = "tc_" + UUID.randomUUID() + "_input.txt";
        String outputFileName = "tc_" + UUID.randomUUID() + "_output.txt";

        String inputPath = storageService.uploadString(request.getInput(), inputFileName, bucketName);
        String outputPath = storageService.uploadString(request.getExpectedOutput(), outputFileName, bucketName);

        TestCaseEntity testCase = TestCaseEntity.builder()
                .problemEntity(problem)
                .input(request.getInput())
                .output(request.getExpectedOutput())
                .inputPath(inputPath)
                .outputPath(outputPath)
                .isSample(request.getIsSample() != null ? request.getIsSample() : false)
                .build();

        TestCaseEntity saved = testCaseRepository.save(testCase);
        log.info("Created test case: id={}, problemId={}", saved.getId(), problemId);

        return TestCaseResponse.from(saved, request.getInput(), request.getExpectedOutput());
    }

    /**
     * 문제의 테스트케이스 목록을 조회한다.
     *
     * @param problemId 문제 ID
     * @return 테스트케이스 목록
     */
    public List<TestCaseResponse> getTestCases(Long problemId) {
        if (!problemRepository.existsById(problemId)) {
            throw new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId);
        }

        return testCaseRepository.findByProblemEntity_Id(problemId).stream()
                .map(tc -> {
                    String input = storageService.readString(tc.getInputPath(), bucketName);
                    String output = storageService.readString(tc.getOutputPath(), bucketName);
                    return TestCaseResponse.from(tc, input, output);
                })
                .collect(Collectors.toList());
    }

    /**
     * 테스트케이스를 삭제한다.
     *
     * @param id 테스트케이스 ID
     */
    @Transactional
    public void deleteTestCase(Long id) {
        TestCaseEntity testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("테스트케이스를 찾을 수 없습니다. id=" + id));

        testCaseRepository.delete(testCase);
        log.info("Deleted test case: id={}", id);
    }

    /**
     * 문제의 모든 테스트케이스를 삭제한다.
     *
     * @param problemId 문제 ID
     */
    @Transactional
    public void deleteAllTestCasesByProblemId(Long problemId) {
        List<TestCaseEntity> testCases = testCaseRepository.findByProblemEntity_Id(problemId);
        testCaseRepository.deleteAll(testCases);
        log.info("Deleted all test cases for problemId={}", problemId);
    }
}
