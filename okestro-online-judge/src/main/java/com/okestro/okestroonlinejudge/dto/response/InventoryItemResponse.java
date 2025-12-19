package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.ShopItemType;
import com.okestro.okestroonlinejudge.domain.UserInventoryEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 인벤토리 아이템 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class InventoryItemResponse {

    private Long inventoryId;
    private Long itemId;
    private ShopItemType type;
    private String name;
    private String description;
    private String itemValue;
    private String previewUrl;
    private String rarity;
    private Boolean isEquipped;
    private Integer purchasedPrice;
    private LocalDateTime purchasedAt;

    public static InventoryItemResponse from(UserInventoryEntity entity) {
        return InventoryItemResponse.builder()
                .inventoryId(entity.getId())
                .itemId(entity.getShopItem().getId())
                .type(entity.getShopItem().getType())
                .name(entity.getShopItem().getName())
                .description(entity.getShopItem().getDescription())
                .itemValue(entity.getShopItem().getItemValue())
                .previewUrl(entity.getShopItem().getPreviewUrl())
                .rarity(entity.getShopItem().getRarity())
                .isEquipped(entity.getIsEquipped())
                .purchasedPrice(entity.getPurchasedPrice())
                .purchasedAt(entity.getCreatedAt())
                .build();
    }
}
