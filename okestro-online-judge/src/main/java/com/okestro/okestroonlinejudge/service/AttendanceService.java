package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.repository.AttendanceRepository;
import com.okestro.okestroonlinejudge.repository.GemTransactionRepository;
import com.okestro.okestroonlinejudge.repository.PointRepository;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 출석 체크 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PointRepository pointRepository;
    private final GemTransactionRepository gemTransactionRepository;
    private final UserRepository userRepository;

    /**
     * 기본 출석 포인트
     */
    private static final int BASE_POINTS = 10;

    /**
     * 기본 출석 젬
     */
    private static final int BASE_GEMS = 10;

    /**
     * 연속 출석 보너스 정책 (일수, 보너스 포인트)
     */
    private static final int[][] STREAK_BONUS = {
            {7, 50},
            {14, 100},
            {30, 300}
    };

    /**
     * 연속 출석 젬 보너스 정책 (일수, 보너스 젬)
     */
    private static final int[][] GEM_STREAK_BONUS = {
            {7, 30},
            {14, 70},
            {30, 200}
    };

    /**
     * username으로 사용자를 찾아 출석 체크.
     *
     * @param username 사용자명 (이메일)
     * @return 출석 체크 결과
     */
    @Transactional
    public AttendanceCheckResponse checkAttendanceByUsername(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        return checkAttendance(user);
    }

    /**
     * username으로 오늘 출석 여부 확인.
     *
     * @param username 사용자명 (이메일)
     * @return 오늘 출석 정보
     */
    public TodayAttendanceResponse getTodayAttendanceByUsername(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        return getTodayAttendance(user);
    }

    /**
     * username으로 월간 출석 현황 조회.
     *
     * @param username 사용자명 (이메일)
     * @param year     년도
     * @param month    월
     * @return 월간 출석 현황
     */
    public MonthlyAttendanceResponse getMonthlyAttendanceByUsername(String username, int year, int month) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        return getMonthlyAttendance(user, year, month);
    }

    /**
     * username으로 포인트 정보 조회.
     *
     * @param username 사용자명 (이메일)
     * @return 포인트 정보
     */
    public PointInfoResponse getPointInfoByUsername(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        return getPointInfo(user);
    }

    /**
     * username으로 포인트 이력 조회.
     *
     * @param username   사용자명 (이메일)
     * @param pageNumber 페이지 번호
     * @param pageSize   페이지 크기
     * @return 포인트 이력 페이지
     */
    public Page<PointHistoryResponse> getPointHistoryByUsername(String username, int pageNumber, int pageSize) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        return getPointHistory(user, pageNumber, pageSize);
    }

    /**
     * 출석 체크.
     *
     * @param user 사용자 엔티티
     * @return 출석 체크 결과
     */
    @Transactional
    public AttendanceCheckResponse checkAttendance(UserEntity user) {
        LocalDate today = LocalDate.now();

        // 이미 오늘 출석했는지 확인
        Optional<AttendanceEntity> todayAttendance = attendanceRepository.findByUserAndAttendanceDate(user, today);
        if (todayAttendance.isPresent()) {
            int currentStreak = todayAttendance.get().getStreakDay();
            return AttendanceCheckResponse.alreadyChecked(currentStreak);
        }

        // 연속 출석 계산
        int currentStreak = calculateCurrentStreak(user, today);

        // 보너스 포인트 계산
        int bonusPoints = calculateBonusPoints(currentStreak);

        // 출석 기록 저장
        AttendanceEntity attendance = AttendanceEntity.builder()
                .user(user)
                .attendanceDate(today)
                .pointsEarned(BASE_POINTS)
                .streakDay(currentStreak)
                .bonusPoints(bonusPoints)
                .build();
        attendanceRepository.save(attendance);

        // 포인트 적립
        savePoints(user, attendance, BASE_POINTS, bonusPoints, currentStreak);

        return AttendanceCheckResponse.success(BASE_POINTS, currentStreak, bonusPoints);
    }

    /**
     * 오늘 출석 여부 확인.
     *
     * @param user 사용자 엔티티
     * @return 오늘 출석 정보
     */
    public TodayAttendanceResponse getTodayAttendance(UserEntity user) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceEntity> todayAttendance = attendanceRepository.findByUserAndAttendanceDate(user, today);

        int currentStreak = calculateCurrentStreak(user, today);

        if (todayAttendance.isPresent()) {
            AttendanceEntity attendance = todayAttendance.get();
            return TodayAttendanceResponse.builder()
                    .checked(true)
                    .currentStreak(attendance.getStreakDay())
                    .todayPoints(attendance.getPointsEarned() + attendance.getBonusPoints())
                    .build();
        }

        return TodayAttendanceResponse.builder()
                .checked(false)
                .currentStreak(currentStreak > 0 ? currentStreak - 1 : 0)
                .todayPoints(0)
                .build();
    }

    /**
     * 월간 출석 현황 조회.
     *
     * @param user  사용자 엔티티
     * @param year  년도
     * @param month 월
     * @return 월간 출석 현황
     */
    public MonthlyAttendanceResponse getMonthlyAttendance(UserEntity user, int year, int month) {
        List<AttendanceEntity> attendances = attendanceRepository.findByUserAndYearAndMonth(user, year, month);

        List<String> attendedDates = attendances.stream()
                .map(a -> a.getAttendanceDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        int currentStreak = calculateCurrentStreak(user, LocalDate.now());
        int longestStreak = attendanceRepository.findMaxStreakDayByUser(user).orElse(0);

        return MonthlyAttendanceResponse.builder()
                .year(year)
                .month(month)
                .attendedDates(attendedDates)
                .totalDays(attendedDates.size())
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .build();
    }

    /**
     * 포인트 정보 조회.
     *
     * @param user 사용자 엔티티
     * @return 포인트 정보
     */
    public PointInfoResponse getPointInfo(UserEntity user) {
        int totalPoints = pointRepository.getTotalPointsByUser(user);
        int earnedPoints = pointRepository.getTotalPointsByUserAndType(user, PointType.EARN);
        int usedPoints = Math.abs(pointRepository.getTotalPointsByUserAndType(user, PointType.USE));

        return PointInfoResponse.builder()
                .totalPoints(totalPoints)
                .earnedPoints(earnedPoints)
                .usedPoints(usedPoints)
                .build();
    }

    /**
     * 포인트 이력 조회.
     *
     * @param user       사용자 엔티티
     * @param pageNumber 페이지 번호
     * @param pageSize   페이지 크기
     * @return 포인트 이력 페이지
     */
    public Page<PointHistoryResponse> getPointHistory(UserEntity user, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PointEntity> points = pointRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return points.map(PointHistoryResponse::from);
    }

    /**
     * 현재 연속 출석 일수 계산.
     */
    private int calculateCurrentStreak(UserEntity user, LocalDate today) {
        Optional<AttendanceEntity> lastAttendance = attendanceRepository.findTopByUserOrderByAttendanceDateDesc(user);

        if (lastAttendance.isEmpty()) {
            return 1; // 첫 출석
        }

        LocalDate lastDate = lastAttendance.get().getAttendanceDate();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, today);

        if (daysBetween == 0) {
            // 오늘 이미 출석
            return lastAttendance.get().getStreakDay();
        } else if (daysBetween == 1) {
            // 연속 출석
            return lastAttendance.get().getStreakDay() + 1;
        } else {
            // 연속 끊김
            return 1;
        }
    }

    /**
     * 연속 출석 보너스 포인트 계산.
     */
    private int calculateBonusPoints(int streakDay) {
        int bonus = 0;
        for (int[] policy : STREAK_BONUS) {
            if (streakDay == policy[0]) {
                bonus = policy[1];
                break;
            }
        }
        return bonus;
    }

    /**
     * 포인트 적립.
     */
    private void savePoints(UserEntity user, AttendanceEntity attendance, int basePoints, int bonusPoints, int streakDay) {
        // 기본 출석 포인트
        PointEntity basePoint = PointEntity.builder()
                .user(user)
                .amount(basePoints)
                .type(PointType.EARN)
                .reason("출석 체크")
                .attendance(attendance)
                .build();
        pointRepository.save(basePoint);

        // 보너스 포인트 (있는 경우)
        if (bonusPoints > 0) {
            PointEntity bonusPoint = PointEntity.builder()
                    .user(user)
                    .amount(bonusPoints)
                    .type(PointType.EARN)
                    .reason(String.format("연속 %d일 출석 보너스", streakDay))
                    .attendance(attendance)
                    .build();
            pointRepository.save(bonusPoint);
        }
    }
}
