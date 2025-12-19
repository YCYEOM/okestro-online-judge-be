package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.UserStatisticsEntity;
import com.okestro.okestroonlinejudge.repository.projection.OrganizationRankingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 사용자 통계 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatisticsEntity, Long> {

    @Query(value = "SELECT u FROM UserStatisticsEntity u " +
            "JOIN FETCH u.userEntity user " +
            "LEFT JOIN FETCH user.tierEntity " +
            "LEFT JOIN FETCH user.organizationEntity " +
            "WHERE u.rankingPoint > 0 OR u.solvedCount > 0 " +
            "ORDER BY u.rankingPoint DESC, u.solvedCount DESC",
            countQuery = "SELECT COUNT(u) FROM UserStatisticsEntity u " +
            "WHERE u.rankingPoint > 0 OR u.solvedCount > 0")
    Page<UserStatisticsEntity> findUserRankings(Pageable pageable);

    @Query("SELECT o.id as organizationId, o.name as organizationName, " +
            "SUM(s.solvedCount) as totalSolvedCount, " +
            "SUM(s.rankingPoint) as totalRankingPoint, " +
            "COUNT(u.id) as userCount " +
            "FROM UserStatisticsEntity s " +
            "JOIN s.userEntity u " +
            "JOIN u.organizationEntity o " +
            "GROUP BY o.id, o.name " +
            "ORDER BY SUM(s.rankingPoint) DESC")
    Page<OrganizationRankingProjection> findOrganizationRankings(Pageable pageable);
}
