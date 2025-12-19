package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 젬 거래 이력 엔티티.
 * 사용자의 젬 획득/사용 이력을 저장합니다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gem_transactions")
public class GemTransactionEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 젬 소유 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 젬 수량 (양수: 획득, 음수: 사용)
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * 거래 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GemTransactionType type;

    /**
     * 거래 사유
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 관련 인벤토리 (구매인 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private UserInventoryEntity inventory;

    /**
     * 거래 후 잔액
     */
    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    @Builder
    public GemTransactionEntity(UserEntity user, Integer amount, GemTransactionType type,
                                 String reason, UserInventoryEntity inventory, Integer balanceAfter) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.inventory = inventory;
        this.balanceAfter = balanceAfter;
    }
}
