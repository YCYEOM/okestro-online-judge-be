package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.AttendanceEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 출석 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    /**
     * 사용자의 특정 날짜 출석 기록 조회.
     *
     * @param user 사용자
     * @param date 날짜
     * @return 출석 기록 Optional
     */
    Optional<AttendanceEntity> findByUserAndAttendanceDate(UserEntity user, LocalDate date);

    /**
     * 사용자의 특정 기간 출석 기록 조회.
     *
     * @param user      사용자
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 출석 기록 목록
     */
    List<AttendanceEntity> findByUserAndAttendanceDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);

    /**
     * 사용자의 가장 최근 출석 기록 조회.
     *
     * @param user 사용자
     * @return 최근 출석 기록 Optional
     */
    Optional<AttendanceEntity> findTopByUserOrderByAttendanceDateDesc(UserEntity user);

    /**
     * 사용자의 전체 출석 일수 조회.
     *
     * @param user 사용자
     * @return 출석 일수
     */
    long countByUser(UserEntity user);

    /**
     * 사용자의 최장 연속 출석 일수 조회.
     *
     * @param user 사용자
     * @return 최장 연속 출석 일수
     */
    @Query("SELECT MAX(a.streakDay) FROM AttendanceEntity a WHERE a.user = :user")
    Optional<Integer> findMaxStreakDayByUser(@Param("user") UserEntity user);

    /**
     * 사용자의 특정 년월 출석 기록 조회.
     *
     * @param user  사용자
     * @param year  년도
     * @param month 월
     * @return 출석 기록 목록
     */
    @Query("SELECT a FROM AttendanceEntity a WHERE a.user = :user AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month ORDER BY a.attendanceDate ASC")
    List<AttendanceEntity> findByUserAndYearAndMonth(@Param("user") UserEntity user, @Param("year") int year, @Param("month") int month);
}
