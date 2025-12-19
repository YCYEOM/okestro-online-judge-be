package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 장착/해제 결과 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class EquipResponse {

    private boolean success;
    private String message;
    private InventoryItemResponse item;

    public static EquipResponse success(String message, InventoryItemResponse item) {
        return EquipResponse.builder()
                .success(true)
                .message(message)
                .item(item)
                .build();
    }

    public static EquipResponse fail(String message) {
        return EquipResponse.builder()
                .success(false)
                .message(message)
                .item(null)
                .build();
    }
}
