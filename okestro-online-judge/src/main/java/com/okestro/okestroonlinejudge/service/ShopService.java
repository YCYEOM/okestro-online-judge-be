package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 상점 서비스.
 * Point가 곧 젬(상점 포인트)입니다.
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserInventoryRepository inventoryRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    /**
     * 상점 아이템 목록 조회 (전체)
     */
    public List<ShopItemResponse> getAllItems(Long userId) {
        List<ShopItemEntity> items = shopItemRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return items.stream()
                .map(item -> ShopItemResponse.from(item, inventoryRepository.existsByUserIdAndShopItemId(userId, item.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 상점 아이템 목록 조회 (타입별)
     */
    public List<ShopItemResponse> getItemsByType(Long userId, ShopItemType type) {
        List<ShopItemEntity> items = shopItemRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(type);
        return items.stream()
                .map(item -> ShopItemResponse.from(item, inventoryRepository.existsByUserIdAndShopItemId(userId, item.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 상점 아이템 목록 조회 (페이징)
     */
    public Page<ShopItemResponse> getItemsPage(Long userId, ShopItemType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());
        Page<ShopItemEntity> items;

        if (type != null) {
            items = shopItemRepository.findByTypeAndIsActiveTrue(type, pageable);
        } else {
            items = shopItemRepository.findByIsActiveTrue(pageable);
        }

        return items.map(item -> ShopItemResponse.from(item, inventoryRepository.existsByUserIdAndShopItemId(userId, item.getId())));
    }

    /**
     * 아이템 상세 조회
     */
    public ShopItemResponse getItemDetail(Long userId, Long itemId) {
        ShopItemEntity item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));
        boolean owned = inventoryRepository.existsByUserIdAndShopItemId(userId, itemId);
        return ShopItemResponse.from(item, owned);
    }

    /**
     * 사용자의 현재 젬(포인트) 잔액 조회
     */
    public int getGemBalance(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return pointRepository.getTotalPointsByUser(user);
    }

    /**
     * 아이템 구매
     */
    @Transactional
    public PurchaseResponse purchaseItem(Long userId, Long itemId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ShopItemEntity item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));

        // 이미 소유한 아이템인지 확인
        if (inventoryRepository.existsByUserIdAndShopItemId(userId, itemId)) {
            int balance = pointRepository.getTotalPointsByUser(user);
            return PurchaseResponse.fail("이미 소유한 아이템입니다.", balance);
        }

        // 아이템이 비활성화 상태인지 확인
        if (!item.getIsActive()) {
            int balance = pointRepository.getTotalPointsByUser(user);
            return PurchaseResponse.fail("구매할 수 없는 아이템입니다.", balance);
        }

        // 젬(포인트) 잔액 확인
        int currentBalance = pointRepository.getTotalPointsByUser(user);
        if (currentBalance < item.getPrice()) {
            return PurchaseResponse.fail("젬이 부족합니다.", currentBalance);
        }

        // 인벤토리에 아이템 추가
        UserInventoryEntity inventory = UserInventoryEntity.builder()
                .user(user)
                .shopItem(item)
                .isEquipped(false)
                .purchasedPrice(item.getPrice())
                .build();
        inventoryRepository.save(inventory);

        // 젬(포인트) 차감 기록
        PointEntity pointUse = PointEntity.builder()
                .user(user)
                .amount(-item.getPrice())
                .type(PointType.USE)
                .reason(item.getName() + " 구매")
                .build();
        pointRepository.save(pointUse);

        int newBalance = currentBalance - item.getPrice();
        return PurchaseResponse.success(newBalance, InventoryItemResponse.from(inventory));
    }

    /**
     * 인벤토리 조회 (전체)
     */
    public List<InventoryItemResponse> getInventory(Long userId) {
        List<UserInventoryEntity> inventory = inventoryRepository.findByUserId(userId);
        return inventory.stream()
                .map(InventoryItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 인벤토리 조회 (타입별)
     */
    public List<InventoryItemResponse> getInventoryByType(Long userId, ShopItemType type) {
        List<UserInventoryEntity> inventory = inventoryRepository.findByUserIdAndItemType(userId, type);
        return inventory.stream()
                .map(InventoryItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 인벤토리 조회 (페이징)
     */
    public Page<InventoryItemResponse> getInventoryPage(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserInventoryEntity> inventory = inventoryRepository.findByUserId(userId, pageable);
        return inventory.map(InventoryItemResponse::from);
    }

    /**
     * 아이템 장착
     */
    @Transactional
    public EquipResponse equipItem(Long userId, Long inventoryId) {
        UserInventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("인벤토리 아이템을 찾을 수 없습니다."));

        // 소유자 확인
        if (!inventory.getUser().getId().equals(userId)) {
            return EquipResponse.fail("권한이 없습니다.");
        }

        // 이미 장착 중인지 확인
        if (inventory.getIsEquipped()) {
            return EquipResponse.fail("이미 장착된 아이템입니다.");
        }

        // 같은 타입의 기존 장착 아이템 해제
        ShopItemType type = inventory.getShopItem().getType();
        inventoryRepository.findEquippedByUserIdAndType(userId, type)
                .ifPresent(UserInventoryEntity::unequip);

        // 새 아이템 장착
        inventory.equip();

        // AVATAR 타입인 경우 UserEntity의 profileImage 필드 업데이트
        if (type == ShopItemType.AVATAR) {
            UserEntity user = inventory.getUser();
            user.updateProfileImage(inventory.getShopItem().getItemValue());
            userRepository.save(user);
        }

        return EquipResponse.success(inventory.getShopItem().getName() + " 장착 완료", InventoryItemResponse.from(inventory));
    }

    /**
     * 아이템 해제
     */
    @Transactional
    public EquipResponse unequipItem(Long userId, Long inventoryId) {
        UserInventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("인벤토리 아이템을 찾을 수 없습니다."));

        // 소유자 확인
        if (!inventory.getUser().getId().equals(userId)) {
            return EquipResponse.fail("권한이 없습니다.");
        }

        // 장착 중이 아닌 경우
        if (!inventory.getIsEquipped()) {
            return EquipResponse.fail("장착되어 있지 않은 아이템입니다.");
        }

        ShopItemType type = inventory.getShopItem().getType();
        inventory.unequip();

        // AVATAR 타입인 경우 UserEntity의 profileImage 필드를 기본값으로 설정
        if (type == ShopItemType.AVATAR) {
            UserEntity user = inventory.getUser();
            user.updateProfileImage(null);  // null로 설정하면 getUserAvatarUrl에서 기본 아바타 사용
            userRepository.save(user);
        }

        return EquipResponse.success(inventory.getShopItem().getName() + " 해제 완료", InventoryItemResponse.from(inventory));
    }

    /**
     * 사용자의 장착된 아이템 목록 조회
     */
    public List<InventoryItemResponse> getEquippedItems(Long userId) {
        List<UserInventoryEntity> equipped = inventoryRepository.findByUserIdAndIsEquippedTrue(userId);
        return equipped.stream()
                .map(InventoryItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 특정 타입 장착 아이템 조회
     */
    public InventoryItemResponse getEquippedItemByType(Long userId, ShopItemType type) {
        return inventoryRepository.findEquippedByUserIdAndType(userId, type)
                .map(InventoryItemResponse::from)
                .orElse(null);
    }
}
