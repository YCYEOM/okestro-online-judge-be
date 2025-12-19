package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 상점 아이템 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "shop_items")
public class ShopItemEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 아이템 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopItemType type;

    /**
     * 아이템 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 아이템 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 가격 (젬)
     */
    @Column(nullable = false)
    private Integer price;

    /**
     * 아이템 값 (아바타 스타일, 색상 코드, 이미지 URL 등)
     */
    @Column(name = "item_value", nullable = false, unique = true)
    private String itemValue;

    /**
     * 미리보기 이미지 URL
     */
    @Column(name = "preview_url")
    private String previewUrl;

    /**
     * 희귀도 (COMMON, RARE, EPIC, LEGENDARY)
     */
    @Column(nullable = false)
    private String rarity;

    /**
     * 판매 여부
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_order")
    private Integer sortOrder;
}
