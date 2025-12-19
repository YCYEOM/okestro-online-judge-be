package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제와 태그의 다대다 관계를 매핑하는 중간 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem_tag_map")
public class ProblemTagMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problemEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private ProblemTagEntity tagEntity;

    public ProblemTagMapEntity(ProblemEntity problemEntity, ProblemTagEntity tagEntity) {
        this.problemEntity = problemEntity;
        this.tagEntity = tagEntity;
    }
}
