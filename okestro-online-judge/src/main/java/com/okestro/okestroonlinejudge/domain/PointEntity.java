package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 엔티티.
 * 사용자의 포인트 적립/사용 이력을 저장한다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class PointEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 포인트 소유 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 포인트 금액 (양수: 적립, 음수: 사용)
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * 포인트 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    /**
     * 포인트 적립/사용 사유
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 관련 출석 기록 (출석 포인트인 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private AttendanceEntity attendance;

    @Builder
    public PointEntity(UserEntity user, Integer amount, PointType type, String reason, AttendanceEntity attendance) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.attendance = attendance;
    }
}
