package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 조직 정보 Repository.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    /**
     * 조직 이름으로 조회.
     */
    Optional<OrganizationEntity> findByName(String name);

    /**
     * 최상위 조직 목록 조회 (parent가 null인 조직).
     */
    List<OrganizationEntity> findByParentIsNullOrderByNameAsc();

    /**
     * 특정 상위 조직의 하위 조직 목록 조회.
     */
    List<OrganizationEntity> findByParentIdOrderByNameAsc(Long parentId);

    /**
     * 특정 depth의 조직 목록 조회.
     */
    List<OrganizationEntity> findByDepthOrderByNameAsc(Integer depth);

    /**
     * 조직과 하위 조직을 모두 조회 (재귀적).
     */
    @Query("SELECT o FROM OrganizationEntity o WHERE o.id = :id OR o.parent.id = :id ORDER BY o.depth, o.name")
    List<OrganizationEntity> findByIdWithChildren(@Param("id") Long id);

    /**
     * 조직 이름 중복 확인.
     */
    boolean existsByName(String name);

    /**
     * 특정 조직의 하위 조직 수 조회.
     */
    long countByParentId(Long parentId);
}

