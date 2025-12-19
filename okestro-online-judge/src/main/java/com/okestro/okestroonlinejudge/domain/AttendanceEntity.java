package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 출석 체크 엔티티.
 * 사용자의 일별 출석 기록을 저장한다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "attendance_date"})
})
public class AttendanceEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 출석한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 출석 날짜
     */
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * 획득한 포인트
     */
    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    /**
     * 출석 당시 연속 출석 일수
     */
    @Column(name = "streak_day", nullable = false)
    private Integer streakDay;

    /**
     * 보너스 포인트 (연속 출석 보너스)
     */
    @Column(name = "bonus_points", nullable = false)
    private Integer bonusPoints;

    @Builder
    public AttendanceEntity(UserEntity user, LocalDate attendanceDate, Integer pointsEarned, Integer streakDay, Integer bonusPoints) {
        this.user = user;
        this.attendanceDate = attendanceDate;
        this.pointsEarned = pointsEarned;
        this.streakDay = streakDay;
        this.bonusPoints = bonusPoints != null ? bonusPoints : 0;
    }
}
