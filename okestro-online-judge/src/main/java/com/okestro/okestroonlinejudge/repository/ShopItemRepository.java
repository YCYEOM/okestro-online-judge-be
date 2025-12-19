package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ShopItemEntity;
import com.okestro.okestroonlinejudge.domain.ShopItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상점 아이템 레포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ShopItemRepository extends JpaRepository<ShopItemEntity, Long> {

    /**
     * 활성화된 아이템만 조회
     */
    List<ShopItemEntity> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 타입별 활성화된 아이템 조회
     */
    List<ShopItemEntity> findByTypeAndIsActiveTrueOrderBySortOrderAsc(ShopItemType type);

    /**
     * 타입별 활성화된 아이템 페이징 조회
     */
    Page<ShopItemEntity> findByTypeAndIsActiveTrue(ShopItemType type, Pageable pageable);

    /**
     * 활성화된 아이템 페이징 조회
     */
    Page<ShopItemEntity> findByIsActiveTrue(Pageable pageable);

    /**
     * 희귀도별 활성화된 아이템 조회
     */
    List<ShopItemEntity> findByRarityAndIsActiveTrueOrderBySortOrderAsc(String rarity);
}
