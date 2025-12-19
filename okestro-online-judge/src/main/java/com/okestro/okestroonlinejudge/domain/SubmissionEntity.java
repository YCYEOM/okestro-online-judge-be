package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 제출 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "submission")
public class SubmissionEntity extends BaseTimeEntity {

    /**
     * 제출 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 제출한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    /**
     * 대상 문제
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problemEntity;

    /**
     * 언어
     */
    @Column(nullable = false)
    private String language;

    /**
     * 소스 코드
     */
    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    /**
     * 채점 결과
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionResult result;

    /**
     * 실행 시간 (ms)
     */
    @Column(name = "exec_time_ms")
    private Integer execTimeMs;

    /**
     * 메모리 사용량 (KB)
     */
    @Column(name = "memory_kb")
    private Integer memoryKb;

    /**
     * Submission 생성자.
     *
     * @param userEntity 사용자
     * @param problemEntity 문제
     * @param language 언어
     * @param sourceCode 소스 코드
     * @param result 결과
     * @param execTimeMs 실행 시간
     * @param memoryKb 메모리 사용량
     */
    @Builder
    public SubmissionEntity(UserEntity userEntity, ProblemEntity problemEntity, String language, String sourceCode, SubmissionResult result, Integer execTimeMs, Integer memoryKb) {
        this.userEntity = userEntity;
        this.problemEntity = problemEntity;
        this.language = language;
        this.sourceCode = sourceCode;
        this.result = result;
        this.execTimeMs = execTimeMs;
        this.memoryKb = memoryKb;
    }

    /**
     * 채점 결과를 업데이트한다.
     *
     * @param result 결과
     * @param execTimeMs 실행 시간
     * @param memoryKb 메모리 사용량
     */
    public void updateResult(SubmissionResult result, Integer execTimeMs, Integer memoryKb) {
        this.result = result;
        this.execTimeMs = execTimeMs;
        this.memoryKb = memoryKb;
    }
}


