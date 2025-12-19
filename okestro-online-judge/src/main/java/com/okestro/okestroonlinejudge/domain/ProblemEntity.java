package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 문제 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem")
public class ProblemEntity extends BaseTimeEntity {

    /**
     * 문제 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 문제 제목
     */
    @Column(nullable = false)
    private String title;

    /**
     * 문제 내용(마크다운) 파일 경로 (MinIO)
     */
    @Column(name = "content_path", nullable = false)
    private String contentPath;

    /**
     * 입력 설명
     */
    @Column(name = "input_desc", columnDefinition = "TEXT")
    private String inputDesc;

    /**
     * 출력 설명
     */
    @Column(name = "output_desc", columnDefinition = "TEXT")
    private String outputDesc;

    /**
     * 난이도(티어) 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private TierEntity tierEntity;

    /**
     * 문제 생성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    /**
     * 문제 태그 목록 (다대다 연결 엔티티를 1대다로 매핑)
     * 주의: 실제 연결 테이블(ProblemTagEntity)이 필요함.
     * 현재 ProblemTagEntity는 이름만 가진 엔티티로 보임.
     * 다대다 관계를 위한 중간 엔티티(ProblemTagMapEntity 등)가 필요하거나,
     * ProblemTagEntity가 중간 테이블 역할을 해야 함.
     *
     * 일단 필터링을 위해 필드만 선언 (Specification에서 사용).
     * JPA 매핑은 나중에 정확히 구현해야 함.
     * 여기서는 Specification에서 join("problemTags")를 사용하므로, 매핑이 필요함.
     */
    @OneToMany(mappedBy = "problemEntity", fetch = FetchType.LAZY)
    private List<ProblemTagMapEntity> problemTags = new ArrayList<>();

    /**
     * 시간 제한 (ms)
     */
    @Column(name = "time_limit_ms", nullable = false)
    private Integer timeLimitMs;

    /**
     * 메모리 제한 (KB)
     */
    @Column(name = "memory_limit_kb", nullable = false)
    private Integer memoryLimitKb;

    /**
     * Problem 생성자.
     *
     * @param title 제목
     * @param contentPath 내용 파일 경로
     * @param inputDesc 입력 설명
     * @param outputDesc 출력 설명
     * @param tierEntity 난이도(티어)
     * @param creator 문제 생성자
     * @param timeLimitMs 시간 제한
     * @param memoryLimitKb 메모리 제한
     */
    @Builder
    public ProblemEntity(String title, String contentPath, String inputDesc, String outputDesc, TierEntity tierEntity, UserEntity creator, Integer timeLimitMs, Integer memoryLimitKb) {
        this.title = title;
        this.contentPath = contentPath;
        this.inputDesc = inputDesc;
        this.outputDesc = outputDesc;
        this.tierEntity = tierEntity;
        this.creator = creator;
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitKb = memoryLimitKb;
    }

    /**
     * 문제 정보를 업데이트한다.
     *
     * @param title 제목
     * @param inputDesc 입력 설명
     * @param outputDesc 출력 설명
     * @param tierEntity 난이도(티어)
     * @param timeLimitMs 시간 제한
     * @param memoryLimitKb 메모리 제한
     */
    public void update(String title, String inputDesc, String outputDesc, TierEntity tierEntity, Integer timeLimitMs, Integer memoryLimitKb) {
        this.title = title;
        this.inputDesc = inputDesc;
        this.outputDesc = outputDesc;
        this.tierEntity = tierEntity;
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitKb = memoryLimitKb;
    }
}
