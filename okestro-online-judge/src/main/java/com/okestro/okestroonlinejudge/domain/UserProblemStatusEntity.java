package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자별 문제 풀이 상태를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_problem_status")
public class UserProblemStatusEntity {

    /**
     * 사용자 문제 상태 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    /**
     * 문제 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problemEntity;

    /**
     * 상태 (SOLVED, ATTEMPTED, NOT)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemStatus status;

    /**
     * 마지막 해결 일시
     */
    @Column(name = "last_solved")
    private LocalDateTime lastSolved;

    /**
     * UserProblemStatus 생성자.
     *
     * @param userEntity 사용자
     * @param problemEntity 문제
     * @param status 상태
     */
    public UserProblemStatusEntity(UserEntity userEntity, ProblemEntity problemEntity, ProblemStatus status) {
        this.userEntity = userEntity;
        this.problemEntity = problemEntity;
        this.status = status;
    }

    /**
     * 상태를 업데이트한다.
     *
     * @param status 새로운 상태
     */
    public void updateStatus(ProblemStatus status) {
        this.status = status;
        if (status == ProblemStatus.SOLVED) {
            this.lastSolved = LocalDateTime.now();
        }
    }
}


