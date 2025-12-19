package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 인벤토리 엔티티.
 * 사용자가 구매한 아이템을 저장합니다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "shop_item_id"})
})
public class UserInventoryEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 구매한 아이템
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_item_id", nullable = false)
    private ShopItemEntity shopItem;

    /**
     * 장착 여부
     */
    @Column(name = "is_equipped", nullable = false)
    private Boolean isEquipped;

    /**
     * 구매 시점의 가격 (젬)
     */
    @Column(name = "purchased_price", nullable = false)
    private Integer purchasedPrice;

    /**
     * 아이템 장착
     */
    public void equip() {
        this.isEquipped = true;
    }

    /**
     * 아이템 해제
     */
    public void unequip() {
        this.isEquipped = false;
    }
}
