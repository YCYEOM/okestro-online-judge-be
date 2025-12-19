package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.client.Judge0Client;
import com.okestro.okestroonlinejudge.domain.PointEntity;
import com.okestro.okestroonlinejudge.domain.PointType;
import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import com.okestro.okestroonlinejudge.domain.UserStatisticsEntity;
import com.okestro.okestroonlinejudge.dto.request.SubmitCodeRequest;
import com.okestro.okestroonlinejudge.dto.response.SubmissionResponse;
import com.okestro.okestroonlinejudge.repository.PointRepository;
import com.okestro.okestroonlinejudge.repository.ProblemRepository;
import com.okestro.okestroonlinejudge.repository.SubmissionRepository;
import com.okestro.okestroonlinejudge.repository.TestCaseRepository;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import com.okestro.okestroonlinejudge.repository.UserStatisticsRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 제출 관리 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final UserStatisticsRepository userStatisticsRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final PointRepository pointRepository;
    private final Judge0Client judge0Client;
    private final StorageService storageService;
    private final TestCaseExecutor testCaseExecutor;

    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${judge0.max-concurrent-executions:5}")
    private int maxConcurrentExecutions;
    
    @Value("${judge0.timeout-multiplier:2.0}")
    private double timeoutMultiplier;
    
    @Value("${judge0.memory-limit:262144}")
    private int memoryLimitKb;
    
    @Value("${judge0.cpu-time-limit:5}")
    private int cpuTimeLimitSec;

    /**
     * 코드를 제출하고 채점합니다.
     *
     * @param request 제출 요청 DTO
     * @return 제출 결과 응답
     */
    @Transactional
    public SubmissionResponse submit(SubmitCodeRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + request.getUserId()));

        ProblemEntity problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + request.getProblemId()));

        // 제출 엔티티 생성 (초기 상태: JUDGING)
        SubmissionEntity submission = SubmissionEntity.builder()
                .userEntity(user)
                .problemEntity(problem)
                .language(request.getLanguage())
                .sourceCode(request.getSourceCode())
                .result(SubmissionResult.JUDGING)
                .build();

        submission = submissionRepository.save(submission);
        log.info("제출 생성 완료: submissionId={}, problemId={}, userId={}",
                submission.getId(), request.getProblemId(), request.getUserId());

        // 채점 수행
        JudgeResultSummary judgeResult = judgeCode(
                request.getProblemId(),
                request.getSourceCode(),
                request.getLanguage()
        );

        // 결과 업데이트
        Integer execTimeMs = judgeResult.getMaxExecutionTime() > 0
                ? (int) (judgeResult.getMaxExecutionTime() * 1000) // 초 -> 밀리초
                : null;
        Integer memoryKb = judgeResult.getMaxMemoryUsage() > 0
                ? judgeResult.getMaxMemoryUsage()
                : null;

        submission.updateResult(judgeResult.getFinalResult(), execTimeMs, memoryKb);

        log.info("채점 완료: submissionId={}, result={}, execTimeMs={}, memoryKb={}",
                submission.getId(), judgeResult.getFinalResult(), execTimeMs, memoryKb);

        // 사용자 통계 업데이트
        log.info("사용자 통계 업데이트 메서드 호출 직전 - userId={}, problemId={}, result={}", 
                user.getId(), problem.getId(), judgeResult.getFinalResult());
        
        try {
            updateUserStatistics(user, problem, judgeResult.getFinalResult());
            log.info("사용자 통계 업데이트 메서드 호출 완료");
        } catch (Exception e) {
            log.error("사용자 통계 업데이트 중 오류 발생", e);
            // 통계 업데이트 실패해도 제출 자체는 성공으로 처리
        }

        // 테스트케이스 정보와 함께 응답 반환
        return SubmissionResponse.fromWithTestCases(
                submission, 
                judgeResult.getTotalTestCases(), 
                judgeResult.getPassedTestCases()
        );
    }
    
    /**
     * 사용자 통계를 업데이트합니다.
     *
     * @param user 사용자 엔티티
     * @param problem 문제 엔티티
     * @param result 제출 결과
     */
    protected void updateUserStatistics(UserEntity user, ProblemEntity problem, SubmissionResult result) {
        log.info("사용자 통계 업데이트 시작 - 사용자 ID: {}, 문제 ID: {}, 결과: {}", 
                user.getId(), problem.getId(), result);
        
        // 문제 해결 성공이 아니면 실패 로그만 기록하고 종료
        if (result != SubmissionResult.ACCEPTED) {
            log.info("문제 풀이 실패 - 사용자 ID: {}, 문제 ID: {}, 결과: {}", 
                    user.getId(), problem.getId(), result);
            return;
        }
        
        // 이미 해결한 문제인지 확인 (현재 제출 이전의 성공 제출이 있는지 확인)
        List<SubmissionEntity> previousAcceptedSubmissions = submissionRepository
                .findByUserEntityIdAndProblemEntityIdAndResult(
                        user.getId(),
                        problem.getId(),
                        SubmissionResult.ACCEPTED
                );
        
        // 현재 제출을 제외하고 이전에 성공한 적이 있는지 확인
        boolean alreadySolved = previousAcceptedSubmissions.size() > 1;

        log.info("이전 성공 제출 건수: {}, 이미 해결한 문제 여부: {}", 
                previousAcceptedSubmissions.size(), alreadySolved);

        // 이미 해결한 문제면 통계 업데이트 안 함
        if (alreadySolved) {
            log.info("이미 해결한 문제 - 사용자 ID: {}, 문제 ID: {}", user.getId(), problem.getId());
            return;
        }

        // 문제의 난이도에 따른 점수 가져오기 (기본값: 10)
        int problemScore = problem.getTierEntity() != null && problem.getTierEntity().getProblemScore() != null
                ? problem.getTierEntity().getProblemScore()
                : 10;

        log.info("문제 점수: {}", problemScore);

        // 사용자 통계 조회 또는 생성
        UserStatisticsEntity userStats = userStatisticsRepository.findById(user.getId())
                .orElseGet(() -> {
                    log.info("사용자 통계가 없어서 새로 생성합니다 - 사용자 ID: {}", user.getId());
                    UserStatisticsEntity newStats = new UserStatisticsEntity(user);
                    return userStatisticsRepository.save(newStats);
                });

        log.info("통계 업데이트 전 - 해결 문제 수: {}, 랭킹 포인트: {}", 
                userStats.getSolvedCount(), userStats.getRankingPoint());

        // 통계 업데이트
        userStats.addSolvedProblem(problemScore);
        userStatisticsRepository.save(userStats);

        log.info("통계 업데이트 완료 - 사용자 ID: {}, 문제 ID: {}, 문제 점수: {}, 새로운 해결 문제 수: {}, 새로운 랭킹 포인트: {}",
                user.getId(), problem.getId(), problemScore, userStats.getSolvedCount(), userStats.getRankingPoint());
    }

    /**
     * 문제 점수에 따른 젬 보상 계산
     * 
     * @param problemScore 문제 점수
     * @return 젬 보상
     */
    private int calculateGemReward(int problemScore) {
        // 기본 공식: 문제 점수 * 5 (예: 10점 문제 = 50젬, 20점 문제 = 100젬)
        // 최소 10젬, 최대 500젬
        int reward = problemScore * 5;
        return Math.max(10, Math.min(500, reward));
    }

    /**
     * 제출 ID로 제출 정보를 조회합니다.
     *
     * @param submissionId 제출 ID
     * @return 제출 응답 DTO
     */
    public SubmissionResponse getSubmission(Long submissionId) {
        SubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출을 찾을 수 없습니다: " + submissionId));
        return SubmissionResponse.from(submission);
    }

    /**
     * 사용자의 제출 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 제출 목록
     */
    public List<SubmissionResponse> getSubmissionsByUser(Long userId) {
        return submissionRepository.findByUserEntity_Id(userId).stream()
                .map(SubmissionResponse::from)
                .toList();
    }

    /**
     * 문제의 제출 목록을 조회합니다.
     *
     * @param problemId 문제 ID
     * @return 제출 목록
     */
    public List<SubmissionResponse> getSubmissionsByProblem(Long problemId) {
        return submissionRepository.findByProblemEntity_Id(problemId).stream()
                .map(SubmissionResponse::from)
                .toList();
    }

    /**
     * 전체 제출 목록을 최신순으로 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 제출 목록
     */
    public List<SubmissionResponse> getAllSubmissions(int page, int size) {
        org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page, size, 
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        
        return submissionRepository.findAll(pageable).stream()
                .map(SubmissionResponse::from)
                .toList();
    }

    /**
     * 코드를 채점합니다.
     *
     * @param problemId  문제 ID
     * @param sourceCode 소스 코드
     * @param language   프로그래밍 언어
     * @return 채점 결과
     */
    public JudgeResultSummary judgeCode(Long problemId, String sourceCode, String language) {
        List<TestCaseEntity> testCases = testCaseRepository.findByProblemEntity_Id(problemId);

        if (testCases.isEmpty()) {
            log.warn("문제 {}에 테스트케이스가 없습니다.", problemId);
            return JudgeResultSummary.noTestCase();
        }

        // 테스트케이스 실행
        List<TestCaseResult> results = testCaseExecutor.executeTestCases(sourceCode, language, testCases);

        // 결과 집계
        int passedCount = (int) results.stream()
                .filter(result -> result.getResult() == SubmissionResult.ACCEPTED)
                .count();

        // 최종 결과 결정
        SubmissionResult finalResult = results.stream()
                .map(TestCaseResult::getResult)
                .filter(result -> result != SubmissionResult.ACCEPTED)
                .findFirst()
                .orElse(SubmissionResult.ACCEPTED);

        // 최대 실행 시간 및 메모리 사용량 계산
        double maxExecutionTime = results.stream()
                .map(TestCaseResult::getExecutionTime)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(0.0);

        int maxMemoryUsage = results.stream()
                .map(TestCaseResult::getMemoryUsage)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        return JudgeResultSummary.builder()
                .finalResult(finalResult)
                .totalTestCases(testCases.size())
                .passedTestCases(passedCount)
                .maxExecutionTime(maxExecutionTime)
                .maxMemoryUsage(maxMemoryUsage)
                .testCaseResults(results)
                .build();
    }

    /**
     * 단일 테스트케이스 채점 (입력/출력 직접 지정).
     */
    public Judge0Client.JudgeResult judgeSingleTestCase(
            String sourceCode, String language, String input, String expectedOutput) {
        return judge0Client.judge(sourceCode, language, input, expectedOutput);
    }

    /**
     * 샘플 테스트케이스만 실행합니다 (제출 기록을 남기지 않음).
     *
     * @param problemId  문제 ID
     * @param sourceCode 소스 코드
     * @param language   프로그래밍 언어
     * @return 샘플 테스트케이스 실행 결과
     */
    public List<TestCaseResult> runSampleTestCases(Long problemId, String sourceCode, String language) {
        // 샘플 테스트케이스만 조회
        List<TestCaseEntity> sampleTestCases = testCaseRepository.findByProblemEntity_Id(problemId).stream()
                .filter(tc -> tc.getIsSample() != null && tc.getIsSample())
                .toList();

        if (sampleTestCases.isEmpty()) {
            log.warn("문제 {}에 샘플 테스트케이스가 없습니다.", problemId);
            return List.of();
        }

        log.info("샘플 테스트케이스 실행 시작 - 문제 ID: {}, 샘플 개수: {}", problemId, sampleTestCases.size());

        // 테스트케이스 실행
        List<TestCaseResult> results = testCaseExecutor.executeTestCases(sourceCode, language, sampleTestCases);

        log.info("샘플 테스트케이스 실행 완료 - 문제 ID: {}, 결과 개수: {}", problemId, results.size());

        return results;
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class JudgeResultSummary {
        private final SubmissionResult finalResult;
        private final int totalTestCases;
        private final int passedTestCases;
        private final double maxExecutionTime;
        private final int maxMemoryUsage;
        private final List<TestCaseResult> testCaseResults;

        public static JudgeResultSummary noTestCase() {
            return JudgeResultSummary.builder()
                    .finalResult(SubmissionResult.RUNTIME_ERROR)
                    .totalTestCases(0)
                    .passedTestCases(0)
                    .testCaseResults(List.of())
                    .build();
        }

        public boolean isAllPassed() {
            return finalResult == SubmissionResult.ACCEPTED;
        }
        
        /**
         * 샘플 테스트케이스 결과만 필터링합니다.
         *
         * @return 샘플 테스트케이스 결과 목록
         */
        public List<TestCaseResult> getSampleResults() {
            return testCaseResults.stream()
                    .filter(TestCaseResult::isSample)
                    .collect(Collectors.toList());
        }
        
        /**
         * 모든 테스트케이스 결과를 정렬하여 반환합니다.
         *
         * @param sortBy 정렬 기준 (time, memory, result)
         * @param ascending 오름차순 여부
         * @return 정렬된 테스트케이스 결과 목록
         */
        public List<TestCaseResult> getSortedResults(String sortBy, boolean ascending) {
            Comparator<TestCaseResult> comparator;
            
            switch (sortBy.toLowerCase()) {
                case "time":
                    comparator = Comparator.comparing(
                        TestCaseResult::getExecutionTime, 
                        Comparator.nullsLast(Double::compareTo)
                    );
                    break;
                case "memory":
                    comparator = Comparator.comparing(
                        TestCaseResult::getMemoryUsage, 
                        Comparator.nullsLast(Integer::compareTo)
                    );
                    break;
                case "result":
                    comparator = Comparator.comparing(TestCaseResult::getResult);
                    break;
                default:
                    comparator = Comparator.comparing(TestCaseResult::getTestCaseNumber);
            }
            
            if (!ascending) {
                comparator = comparator.reversed();
            }
            
            return testCaseResults.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class TestCaseResult {
        /** 테스트케이스 ID (또는 번호) */
        private final Long testCaseId;
        
        /** 테스트케이스 번호 */
        private final int testCaseNumber;
        
        /** 채점 결과 */
        private final SubmissionResult result;
        
        /** 실행 시간 (초) */
        private final Double executionTime;
        
        /** 메모리 사용량 (KB) */
        private final Integer memoryUsage;
        
        /** 표준 출력 */
        private final String stdout;
        
        /** 표준 에러 */
        private final String stderr;
        
        /** 입력 데이터 */
        private final String input;
        
        /** 예상 출력 */
        private final String expectedOutput;
        
        /** 실제 출력 */
        private final String actualOutput;
        
        /** 에러 메시지 */
        private final String errorMessage;
        
        /** 샘플 테스트케이스 여부 */
        private final boolean isSample;
        
        /**
         * 테스트케이스가 성공했는지 여부를 반환합니다.
         *
         * @return 성공 여부
         */
        public boolean isPassed() {
            return result == SubmissionResult.ACCEPTED;
        }
        
        /**
         * 실행 시간을 밀리초 단위로 반환합니다.
         *
         * @return 밀리초 단위 실행 시간 (없으면 0.0)
         */
        public double getExecutionTimeMs() {
            return executionTime != null ? executionTime * 1000 : 0.0;
        }
        
        /**
         * 메모리 사용량을 MB 단위로 반환합니다.
         *
         * @return MB 단위 메모리 사용량 (없으면 0.0)
         */
        public double getMemoryUsageMb() {
            return memoryUsage != null ? memoryUsage / 1024.0 : 0.0;
        }
    }
}


