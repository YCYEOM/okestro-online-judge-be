package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.ShopItemEntity;
import com.okestro.okestroonlinejudge.domain.ShopItemType;
import lombok.Builder;
import lombok.Getter;

/**
 * 상점 아이템 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class ShopItemResponse {

    private Long id;
    private ShopItemType type;
    private String name;
    private String description;
    private Integer price;
    private String itemValue;
    private String previewUrl;
    private String rarity;
    private Boolean owned;

    public static ShopItemResponse from(ShopItemEntity entity, boolean owned) {
        return ShopItemResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .itemValue(entity.getItemValue())
                .previewUrl(entity.getPreviewUrl())
                .rarity(entity.getRarity())
                .owned(owned)
                .build();
    }
}
