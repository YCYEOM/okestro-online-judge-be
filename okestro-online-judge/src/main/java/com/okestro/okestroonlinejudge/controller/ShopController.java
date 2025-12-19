package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.domain.ShopItemType;
import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.service.ShopService;
import com.okestro.okestroonlinejudge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상점 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Shop", description = "상점 관련 API")
@RestController
@RequestMapping("/oj/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final UserService userService;

    /**
     * 상점 아이템 목록 조회
     */
    @Operation(summary = "상점 아이템 목록", description = "구매 가능한 상점 아이템 목록을 조회합니다.")
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShopItemResponse>>> getItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ShopItemType type
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            List<ShopItemResponse> items;

            if (type != null) {
                items = shopService.getItemsByType(userId, type);
            } else {
                items = shopService.getAllItems(userId);
            }

            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 상점 아이템 목록 조회 (페이징)
     */
    @Operation(summary = "상점 아이템 목록 (페이징)", description = "구매 가능한 상점 아이템 목록을 페이징하여 조회합니다.")
    @GetMapping("/items/page")
    public ResponseEntity<ApiResponse<Page<ShopItemResponse>>> getItemsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ShopItemType type,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            Page<ShopItemResponse> items = shopService.getItemsPage(userId, type, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 아이템 상세 조회
     */
    @Operation(summary = "아이템 상세", description = "특정 아이템의 상세 정보를 조회합니다.")
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<ShopItemResponse>> getItemDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            ShopItemResponse item = shopService.getItemDetail(userId, itemId);
            return ResponseEntity.ok(ApiResponse.success(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 젬 잔액 조회
     */
    @Operation(summary = "젬 잔액 조회", description = "현재 사용자의 젬 잔액을 조회합니다.")
    @GetMapping("/gems/balance")
    public ResponseEntity<ApiResponse<Integer>> getGemBalance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            int balance = shopService.getGemBalance(userId);
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 아이템 구매
     */
    @Operation(summary = "아이템 구매", description = "젬을 사용하여 아이템을 구매합니다.")
    @PostMapping("/items/{itemId}/purchase")
    public ResponseEntity<ApiResponse<PurchaseResponse>> purchaseItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            PurchaseResponse result = shopService.purchaseItem(userId, itemId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 인벤토리 조회
     */
    @Operation(summary = "인벤토리 조회", description = "구매한 아이템 목록을 조회합니다.")
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getInventory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ShopItemType type
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            List<InventoryItemResponse> items;

            if (type != null) {
                items = shopService.getInventoryByType(userId, type);
            } else {
                items = shopService.getInventory(userId);
            }

            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 인벤토리 조회 (페이징)
     */
    @Operation(summary = "인벤토리 조회 (페이징)", description = "구매한 아이템 목록을 페이징하여 조회합니다.")
    @GetMapping("/inventory/page")
    public ResponseEntity<ApiResponse<Page<InventoryItemResponse>>> getInventoryPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            Page<InventoryItemResponse> items = shopService.getInventoryPage(userId, pageNumber, pageSize);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 장착된 아이템 조회 (본인)
     */
    @Operation(summary = "장착된 아이템 조회", description = "현재 장착된 아이템 목록을 조회합니다.")
    @GetMapping("/equipped")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getEquippedItems(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            List<InventoryItemResponse> items = shopService.getEquippedItems(userId);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 다른 유저의 장착된 아이템 조회
     */
    @Operation(summary = "다른 유저의 장착된 아이템 조회", description = "특정 사용자가 장착한 아이템 목록을 조회합니다.")
    @GetMapping("/equipped/{userId}")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getUserEquippedItems(
            @PathVariable Long userId
    ) {
        try {
            List<InventoryItemResponse> items = shopService.getEquippedItems(userId);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 아이템 장착
     */
    @Operation(summary = "아이템 장착", description = "인벤토리의 아이템을 장착합니다.")
    @PostMapping("/inventory/{inventoryId}/equip")
    public ResponseEntity<ApiResponse<EquipResponse>> equipItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long inventoryId
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            EquipResponse result = shopService.equipItem(userId, inventoryId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 아이템 해제
     */
    @Operation(summary = "아이템 해제", description = "장착된 아이템을 해제합니다.")
    @PostMapping("/inventory/{inventoryId}/unequip")
    public ResponseEntity<ApiResponse<EquipResponse>> unequipItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long inventoryId
    ) {
        try {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            EquipResponse result = shopService.unequipItem(userId, inventoryId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
