package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ShopItemType;
import com.okestro.okestroonlinejudge.domain.UserInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 인벤토리 레포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventoryEntity, Long> {

    /**
     * 사용자의 모든 인벤토리 조회
     */
    List<UserInventoryEntity> findByUserId(Long userId);

    /**
     * 사용자의 인벤토리 페이징 조회
     */
    Page<UserInventoryEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 사용자의 특정 타입 아이템 조회
     */
    @Query("SELECT ui FROM UserInventoryEntity ui JOIN ui.shopItem si WHERE ui.user.id = :userId AND si.type = :type")
    List<UserInventoryEntity> findByUserIdAndItemType(@Param("userId") Long userId, @Param("type") ShopItemType type);

    /**
     * 사용자가 특정 아이템을 소유하는지 확인
     */
    boolean existsByUserIdAndShopItemId(Long userId, Long shopItemId);

    /**
     * 사용자의 특정 아이템 조회
     */
    Optional<UserInventoryEntity> findByUserIdAndShopItemId(Long userId, Long shopItemId);

    /**
     * 사용자의 장착된 아이템 조회
     */
    List<UserInventoryEntity> findByUserIdAndIsEquippedTrue(Long userId);

    /**
     * 사용자의 특정 타입 중 장착된 아이템 조회
     */
    @Query("SELECT ui FROM UserInventoryEntity ui JOIN ui.shopItem si WHERE ui.user.id = :userId AND si.type = :type AND ui.isEquipped = true")
    Optional<UserInventoryEntity> findEquippedByUserIdAndType(@Param("userId") Long userId, @Param("type") ShopItemType type);
}
