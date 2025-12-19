package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트케이스 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "testcase")
public class TestCaseEntity {

    /**
     * 테스트케이스 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 문제 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problemEntity;

    /**
     * 입력 데이터 (직접 저장, 작은 데이터용)
     */
    @Column(name = "input", columnDefinition = "TEXT")
    private String input;

    /**
     * 예상 출력 데이터 (직접 저장, 작은 데이터용)
     */
    @Column(name = "output", columnDefinition = "TEXT", nullable = false)
    private String output;

    /**
     * 입력 파일 경로 (MinIO, 큰 데이터용)
     */
    @Column(name = "input_path")
    private String inputPath;

    /**
     * 출력 파일 경로 (MinIO, 큰 데이터용)
     */
    @Column(name = "output_path")
    private String outputPath;

    /**
     * 예제 여부
     */
    @Column(name = "is_sample", nullable = false)
    private Boolean isSample;

    /**
     * TestCase 생성자.
     *
     * @param problemEntity 문제
     * @param inputPath 입력 파일 경로
     * @param outputPath 출력 파일 경로
     * @param isSample 예제 여부
     */
    @Builder
    public TestCaseEntity(ProblemEntity problemEntity, String input, String output,
                          String inputPath, String outputPath, Boolean isSample) {
        this.problemEntity = problemEntity;
        this.input = input;
        this.output = output;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.isSample = isSample;
    }
}


