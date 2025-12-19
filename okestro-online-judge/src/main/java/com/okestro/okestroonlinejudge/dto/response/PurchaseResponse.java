package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 구매 결과 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class PurchaseResponse {

    private boolean success;
    private String message;
    private Integer remainingGems;
    private InventoryItemResponse purchasedItem;

    public static PurchaseResponse success(int remainingGems, InventoryItemResponse item) {
        return PurchaseResponse.builder()
                .success(true)
                .message("구매가 완료되었습니다.")
                .remainingGems(remainingGems)
                .purchasedItem(item)
                .build();
    }

    public static PurchaseResponse fail(String message, int remainingGems) {
        return PurchaseResponse.builder()
                .success(false)
                .message(message)
                .remainingGems(remainingGems)
                .purchasedItem(null)
                .build();
    }
}
