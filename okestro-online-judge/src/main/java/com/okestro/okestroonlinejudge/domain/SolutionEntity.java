package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정답 코드(Solution) 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "solution")
public class SolutionEntity extends BaseTimeEntity {

    /**
     * 정답 코드 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 관련 문제 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    /**
     * 관련 제출 정보 (어떤 제출에 대한 공유인지)
     * 하나의 제출은 하나의 솔루션만 가질 수 있다고 가정 (OneToOne)하거나,
     * 여러 솔루션 가능하게 하려면 ManyToOne. 보통 제출 하나당 공유 하나가 깔끔함.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private SubmissionEntity submission;

    /**
     * 언어 이름 (예: "Java", "Python")
     * Submission에도 있지만, 검색 편의성을 위해 저장할 수 있음.
     * 일단은 Submission을 통해 조회하도록 하고 여기선 생략 가능하지만,
     * FE 필터링 요구사항(언어별 필터링)을 위해 컬럼으로 두는 것이 인덱싱에 유리함.
     */
    @Column(nullable = false)
    private String language;

    /**
     * 소스 코드
     * Submission에 있는 코드를 그대로 쓸 수도 있지만,
     * 사용자가 공유를 위해 주석을 달거나 수정할 수 있으므로 별도 저장.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    /**
     * 설명 (선택 사항)
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 공개 여부
     */
    @Column(nullable = false)
    private boolean visibility;

    /**
     * Solution 생성자.
     *
     * @param user 작성자
     * @param problem 관련 문제
     * @param submission 관련 제출
     * @param language 언어
     * @param code 소스 코드
     * @param description 설명
     * @param visibility 공개 여부
     */
    @Builder
    public SolutionEntity(UserEntity user, ProblemEntity problem, SubmissionEntity submission,
                          String language, String code, String description, boolean visibility) {
        this.user = user;
        this.problem = problem;
        this.submission = submission;
        this.language = language;
        this.code = code;
        this.description = description;
        this.visibility = visibility;
    }

    /**
     * 설명과 공개 여부를 수정한다.
     *
     * @param description 수정할 설명
     * @param visibility 공개 여부
     */
    public void update(String description, boolean visibility) {
        this.description = description;
        this.visibility = visibility;
    }
}

