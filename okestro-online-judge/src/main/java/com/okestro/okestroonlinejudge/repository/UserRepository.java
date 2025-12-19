package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 사용자명으로 사용자 조회.
     *
     * @param username 사용자명
     * @return 사용자 엔티티 Optional
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 이메일로 사용자 조회.
     *
     * @param email 이메일
     * @return 사용자 엔티티 Optional
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인.
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 닉네임 존재 여부 확인.
     *
     * @param nickname 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 닉네임으로 사용자 조회.
     *
     * @param nickname 닉네임
     * @return 사용자 엔티티 Optional
     */
    Optional<UserEntity> findByNickname(String nickname);

    /**
     * 특정 조직에 속한 사용자 목록 조회.
     *
     * @param organizationId 조직 ID
     * @return 사용자 목록
     */
    List<UserEntity> findByOrganizationEntityId(Long organizationId);

    /**
     * 특정 조직에 속한 사용자 수 조회.
     *
     * @param organizationId 조직 ID
     * @return 사용자 수
     */
    long countByOrganizationEntityId(Long organizationId);
}


