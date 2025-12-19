package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.SolutionEntity;
import com.okestro.okestroonlinejudge.domain.SolutionLikeEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 솔루션 좋아요(SolutionLike) 데이터에 접근하기 위한 리포지토리 인터페이스.
 */
public interface SolutionLikeRepository extends JpaRepository<SolutionLikeEntity, Long> {

    boolean existsBySolutionAndUser(SolutionEntity solution, UserEntity user);

    Optional<SolutionLikeEntity> findBySolutionAndUser(SolutionEntity solution, UserEntity user);

    long countBySolution(SolutionEntity solution);
}

