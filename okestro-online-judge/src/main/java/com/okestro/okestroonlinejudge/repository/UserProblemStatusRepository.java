package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.UserProblemStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 문제 풀이 상태 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface UserProblemStatusRepository extends JpaRepository<UserProblemStatusEntity, Long> {

    /**
     * 사용자와 문제로 상태 조회.
     *
     * @param userId 사용자 ID
     * @param problemId 문제 ID
     * @return 상태 엔티티 Optional
     */
    Optional<UserProblemStatusEntity> findByUserEntity_IdAndProblemEntity_Id(Long userId, Long problemId);
}


