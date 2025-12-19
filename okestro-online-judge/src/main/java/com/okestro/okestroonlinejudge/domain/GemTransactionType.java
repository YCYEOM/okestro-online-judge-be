package com.okestro.okestroonlinejudge.domain;

/**
 * 젬 거래 유형 열거형.
 *
 * @author Assistant
 * @since 1.0
 */
public enum GemTransactionType {
    /**
     * 젬 획득 (출석, 문제 풀이 보너스 등)
     */
    EARN,

    /**
     * 젬 사용 (상점 구매)
     */
    PURCHASE,

    /**
     * 젬 환불
     */
    REFUND,

    /**
     * 관리자 지급
     */
    ADMIN_GRANT,

    /**
     * 관리자 차감
     */
    ADMIN_DEDUCT
}
