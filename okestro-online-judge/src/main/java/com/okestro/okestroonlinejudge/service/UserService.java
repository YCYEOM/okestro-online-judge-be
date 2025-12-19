package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.request.UpdatePrivacyRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateProfileRequest;
import com.okestro.okestroonlinejudge.dto.response.*;
import com.okestro.okestroonlinejudge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 사용자 관련 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final SubmissionRepository submissionRepository;
    private final PointRepository pointRepository;
    private final AttendanceRepository attendanceRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final UserInventoryRepository userInventoryRepository;

    /**
     * username으로 프로필 조회.
     */
    public UserProfileResponse getProfileByUsername(String username) {
        UserEntity user = findUserByUsername(username);
        return UserProfileResponse.from(user);
    }

    /**
     * 프로필 정보 수정 (이름 제외).
     */
    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        UserEntity user = findUserByUsername(username);

        // 이름(username)은 변경 불가, 조직과 자기소개만 변경
        user.updateProfile(
                user.getUsername(), // 기존 이름 유지
                request.getOrganization(),
                request.getBio()
        );

        return UserProfileResponse.from(user);
    }

    /**
     * 닉네임 변경 (닉네임 변경권 필요).
     */
    @Transactional
    public UserProfileResponse updateNickname(String username, String newNickname) {
        UserEntity user = findUserByUsername(username);

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 닉네임 변경권 확인 및 사용
        List<UserInventoryEntity> nicknameChangeItems = userInventoryRepository
                .findByUserIdAndItemType(user.getId(), ShopItemType.NICKNAME_CHANGE);

        if (nicknameChangeItems.isEmpty()) {
            throw new IllegalArgumentException("닉네임 변경권이 없습니다. 상점에서 구매해주세요.");
        }

        // 첫 번째 닉네임 변경권 사용 (소모)
        UserInventoryEntity item = nicknameChangeItems.get(0);
        userInventoryRepository.delete(item);

        // 닉네임 변경
        user.updateNickname(newNickname);

        return UserProfileResponse.from(user);
    }

    /**
     * 프로필 이미지 업로드.
     */
    @Transactional
    public String uploadProfileImage(String username, MultipartFile file) {
        UserEntity user = findUserByUsername(username);
        String fileName = imageService.uploadImage(file);
        
        // MinIO URL 생성
        String imageUrl = "/api/images/" + fileName;
        
        user.updateProfileImage(imageUrl);
        return imageUrl;
    }

    /**
     * 프로필 이미지 삭제.
     */
    @Transactional
    public void deleteProfileImage(String username) {
        UserEntity user = findUserByUsername(username);
        user.updateProfileImage(null);
    }

    /**
     * 회원 탈퇴.
     */
    @Transactional
    public void withdrawUser(String username) {
        UserEntity user = findUserByUsername(username);
        
        // 중요 정보 익명화 (Soft Delete 방식)
        String uuid = UUID.randomUUID().toString();
        String deletedUsername = "deleted_" + uuid.substring(0, 8);
        String deletedEmail = "deleted_" + uuid + "@deleted.com";
        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        
        user.withdraw(deletedUsername, deletedEmail, randomPassword);
    }

    /**
     * 사용자 제출 이력 조회 (페이징).
     */
    public Page<SubmissionResponse> getSubmissionsByUsername(String username, int pageNumber, int pageSize) {
        UserEntity user = findUserByUsername(username);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<SubmissionEntity> submissions = submissionRepository.findByUserEntity_Id(user.getId(), pageable);
        return submissions.map(SubmissionResponse::from);
    }

    /**
     * 사용자 스트릭 조회.
     *
     * @param username 사용자 이름
     * @param year     조회 연도 (null인 경우 최근 1년)
     * @return 스트릭 정보
     */
    public UserStreakResponse getUserStreak(String username, Integer year) {
        UserEntity user = findUserByUsername(username);

        LocalDate end = LocalDate.now();
        LocalDate start;

        if (year != null) {
            start = LocalDate.of(year, 1, 1);
            end = LocalDate.of(year, 12, 31);
            if (end.isAfter(LocalDate.now())) {
                end = LocalDate.now();
            }
        } else {
            start = end.minusYears(1).plusDays(1);
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<StreakDateDto> streaks = submissionRepository.findDailySolvedCount(
                user.getId(), SubmissionResult.ACCEPTED, startDateTime, endDateTime);

        long totalSolved = 0;
        int maxStreak = 0;
        int currentStreak = 0;

        if (!streaks.isEmpty()) {
            streaks.sort(Comparator.comparing(StreakDateDto::getDate));

            Set<String> solvedDates = streaks.stream()
                    .map(StreakDateDto::getDate)
                    .collect(Collectors.toSet());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            long tempTotal = 0;
            int tempStreak = 0;

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                String dateStr = date.format(formatter);
                if (solvedDates.contains(dateStr)) {
                    tempStreak++;
                    // 해당 날짜의 count 찾기 (Set 사용 시 count 정보 유실되므로 Map 변환이 더 효율적일 수 있음)
                    // 하지만 streaks 리스트 크기가 작으므로 stream filter도 수용 가능, 혹은 별도 계산
                } else {
                    tempStreak = 0;
                }
                maxStreak = Math.max(maxStreak, tempStreak);
            }
            
            totalSolved = streaks.stream().mapToLong(StreakDateDto::getCount).sum();

            // 현재 스트릭 계산 (오늘 포함 역순)
            LocalDate today = LocalDate.now();
            if (!end.isBefore(today.minusDays(1))) { // 조회 기간이 최근을 포함할 때만
                LocalDate d = today;
                if (!solvedDates.contains(d.format(formatter))) {
                    d = d.minusDays(1); // 오늘 안풀었으면 어제부터 확인
                }
                
                while (solvedDates.contains(d.format(formatter))) {
                    currentStreak++;
                    d = d.minusDays(1);
                }
            }
        }

        return UserStreakResponse.of(
                year != null ? year : LocalDate.now().getYear(),
                totalSolved,
                maxStreak,
                currentStreak,
                streaks
        );
    }

    /**
     * username으로 통계 조회.
     */
    public UserStatsResponse getStatsByUsername(String username) {
        UserEntity user = findUserByUsername(username);
        return buildUserStats(user);
    }

    /**
     * username으로 시도한 문제 목록 조회.
     */
    public Page<AttemptedProblemResponse> getAttemptedProblemsByUsername(String username, int pageNumber, int pageSize) {
        UserEntity user = findUserByUsername(username);
        return getAttemptedProblems(user, pageNumber, pageSize);
    }

    /**
     * 사용자 통계 빌드.
     */
    private UserStatsResponse buildUserStats(UserEntity user) {
        // 기본 통계
        List<SubmissionEntity> submissions = submissionRepository.findByUserEntity_Id(user.getId());
        int totalSubmissions = submissions.size();

        // 문제별 최종 결과 계산
        Map<Long, SubmissionResult> problemResults = new HashMap<>();
        for (SubmissionEntity sub : submissions) {
            Long problemId = sub.getProblemEntity().getId();
            SubmissionResult current = problemResults.get(problemId);
            if (current == null || sub.getResult() == SubmissionResult.ACCEPTED) {
                problemResults.put(problemId, sub.getResult());
            }
        }

        int solvedCount = (int) problemResults.values().stream()
                .filter(r -> r == SubmissionResult.ACCEPTED)
                .count();
        int failedCount = problemResults.size() - solvedCount;

        // 정답률 계산
        double acceptanceRate = totalSubmissions > 0
                ? (double) submissions.stream().filter(s -> s.getResult() == SubmissionResult.ACCEPTED).count() / totalSubmissions * 100
                : 0;

        // 난이도별 카운트
        int easyCount = 0, mediumCount = 0, hardCount = 0;
        for (Map.Entry<Long, SubmissionResult> entry : problemResults.entrySet()) {
            if (entry.getValue() == SubmissionResult.ACCEPTED) {
                SubmissionEntity sub = submissions.stream()
                        .filter(s -> s.getProblemEntity().getId().equals(entry.getKey()))
                        .findFirst().orElse(null);
                if (sub != null && sub.getProblemEntity().getTierEntity() != null) {
                    String groupName = sub.getProblemEntity().getTierEntity().getGroupName();
                    if (groupName != null) {
                        if (groupName.equalsIgnoreCase("BRONZE") || groupName.equalsIgnoreCase("SILVER")) {
                            easyCount++;
                        } else if (groupName.equalsIgnoreCase("GOLD") || groupName.equalsIgnoreCase("PLATINUM")) {
                            mediumCount++;
                        } else {
                            hardCount++;
                        }
                    }
                }
            }
        }

        // 랭킹 계산
        List<UserStatisticsEntity> allStats = userStatisticsRepository.findAll();
        allStats.sort((a, b) -> Long.compare(b.getRankingPoint(), a.getRankingPoint()));
        int rank = 1;
        for (int i = 0; i < allStats.size(); i++) {
            if (allStats.get(i).getUserEntity().getId().equals(user.getId())) {
                rank = i + 1;
                break;
            }
        }
        int totalUsers = (int) userRepository.count();

        // 티어 정보
        String tier = "BRONZE";
        int tierLevel = 5;
        int currentXP = 0;
        int xpToNextTier = 100;

        if (user.getTierEntity() != null) {
            tier = user.getTierEntity().getGroupName().toUpperCase();
            tierLevel = user.getTierEntity().getLevel();
            currentXP = user.getTierEntity().getPowerScore() != null ? user.getTierEntity().getPowerScore() : 0;
            xpToNextTier = user.getTierEntity().getMaxScore() != null ? user.getTierEntity().getMaxScore() : 100;
        }

        // 스트릭 계산
        int streak = 0;
        boolean isStreakActive = false;
        Optional<AttendanceEntity> lastAttendance = attendanceRepository.findTopByUserOrderByAttendanceDateDesc(user);
        if (lastAttendance.isPresent()) {
            streak = lastAttendance.get().getStreakDay();
            LocalDate today = LocalDate.now();
            LocalDate lastDate = lastAttendance.get().getAttendanceDate();
            isStreakActive = lastDate.equals(today);
        }

        // 포인트 정보
        int problemPoints = 0;
        Optional<UserStatisticsEntity> userStats = userStatisticsRepository.findById(user.getId());
        if (userStats.isPresent()) {
            problemPoints = userStats.get().getRankingPoint().intValue();
        }

        int shopPoints = pointRepository.getTotalPointsByUser(user); // 출석 등으로 얻은 젬

        return UserStatsResponse.builder()
                .rank(rank)
                .totalUsers(totalUsers)
                .solvedCount(solvedCount)
                .failedCount(failedCount)
                .totalSubmissions(totalSubmissions)
                .acceptanceRate(Math.round(acceptanceRate * 10) / 10.0)
                .easyCount(easyCount)
                .mediumCount(mediumCount)
                .hardCount(hardCount)
                .tier(tier)
                .tierLevel(tierLevel)
                .currentXP(currentXP)
                .xpToNextTier(xpToNextTier)
                .streak(streak)
                .isStreakActive(isStreakActive)
                .problemPoints(problemPoints)
                .shopPoints(shopPoints)
                .build();
    }

    /**
     * 시도한 문제 목록 조회.
     */
    private Page<AttemptedProblemResponse> getAttemptedProblems(UserEntity user, int pageNumber, int pageSize) {
        List<SubmissionEntity> allSubmissions = submissionRepository.findByUserEntity_Id(user.getId());

        // 문제별로 그룹화하여 최신 제출과 시도 횟수 계산
        Map<Long, List<SubmissionEntity>> byProblem = allSubmissions.stream()
                .collect(Collectors.groupingBy(s -> s.getProblemEntity().getId()));

        List<AttemptedProblemResponse> problems = new ArrayList<>();
        for (Map.Entry<Long, List<SubmissionEntity>> entry : byProblem.entrySet()) {
            List<SubmissionEntity> subs = entry.getValue();
            subs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            SubmissionEntity latest = subs.get(0);
            int tryCount = subs.size();
            problems.add(AttemptedProblemResponse.from(latest, tryCount));
        }

        // 최신순 정렬
        problems.sort((a, b) -> b.getAttemptedAt().compareTo(a.getAttemptedAt()));

        // 페이징
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), problems.size());

        List<AttemptedProblemResponse> pageContent = start < problems.size()
                ? problems.subList(start, end)
                : Collections.emptyList();

        return new PageImpl<>(pageContent, pageable, problems.size());
    }

    /**
     * username으로 사용자 ID 조회.
     */
    public Long getUserIdByUsername(String username) {
        return findUserByUsername(username).getId();
    }

    /**
     * username(email 또는 username)으로 사용자 조회.
     */
    private UserEntity findUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
    }

    // ==================== 프로필 공개 설정 API ====================

    /**
     * 프로필 공개 설정 조회.
     */
    public PrivacySettingsResponse getPrivacySettings(String username) {
        UserEntity user = findUserByUsername(username);
        return PrivacySettingsResponse.from(user);
    }

    /**
     * 프로필 공개 설정 변경.
     */
    @Transactional
    public PrivacySettingsResponse updatePrivacySettings(String username,
            Boolean profilePublic, Boolean statsPublic,
            Boolean solvedProblemsPublic, Boolean activityPublic) {
        UserEntity user = findUserByUsername(username);
        user.updatePrivacySettings(profilePublic, statsPublic, solvedProblemsPublic, activityPublic);
        return PrivacySettingsResponse.from(user);
    }

    // ==================== 다른 사용자 프로필 조회 API ====================

    /**
     * 다른 사용자의 공개 프로필 조회.
     *
     * @param userId 조회할 사용자 ID
     * @return 공개 프로필 정보
     */
    public PublicProfileResponse getPublicProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 프로필 비공개인 경우 기본 정보만 반환
        if (!user.getProfilePublic()) {
            return PublicProfileResponse.builder()
                    .id(user.getId())
                    .userName(user.getUsername())
                    .profileImage(user.getProfileImage())
                    .createdAt(user.getCreatedAt())
                    .statsPublic(false)
                    .solvedProblemsPublic(false)
                    .activityPublic(false)
                    .createdProblemsCount(user.getCreatedProblemsCount())
                    .sharedSolutionsCount(user.getSharedSolutionsCount())
                    .build();
        }

        PublicProfileResponse.PublicProfileResponseBuilder builder = PublicProfileResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .profileImage(user.getProfileImage())
                .organization(user.getOrganizationName())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .statsPublic(user.getStatsPublic())
                .solvedProblemsPublic(user.getSolvedProblemsPublic())
                .activityPublic(user.getActivityPublic())
                .createdProblemsCount(user.getCreatedProblemsCount())
                .sharedSolutionsCount(user.getSharedSolutionsCount());

        // 통계 공개 시 통계 정보 추가
        if (user.getStatsPublic()) {
            UserStatsResponse stats = buildUserStats(user);
            builder.rank(stats.getRank())
                    .solvedCount(stats.getSolvedCount())
                    .acceptanceRate(stats.getAcceptanceRate())
                    .tier(stats.getTier())
                    .tierLevel(stats.getTierLevel());
        }

        // 활동 공개 시 활동 정보 추가
        if (user.getActivityPublic()) {
            UserStatsResponse stats = buildUserStats(user);
            builder.streak(stats.getStreak())
                    .isStreakActive(stats.isStreakActive());
        }

        return builder.build();
    }

    /**
     * username으로 다른 사용자의 공개 프로필 조회.
     */
    public PublicProfileResponse getPublicProfileByUsername(String targetUsername) {
        UserEntity user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return getPublicProfile(user.getId());
    }

    /**
     * 다른 사용자의 풀이 기록 조회 (공개 설정에 따름).
     */
    public Page<AttemptedProblemResponse> getPublicAttemptedProblems(Long userId, int pageNumber, int pageSize) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비공개인 경우 빈 결과 반환
        if (!user.getProfilePublic() || !user.getSolvedProblemsPublic()) {
            return Page.empty();
        }

        return getAttemptedProblems(user, pageNumber, pageSize);
    }

    /**
     * 다른 사용자의 통계 조회 (공개 설정에 따름).
     */
    public UserStatsResponse getPublicStats(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비공개인 경우 제한된 정보만 반환
        if (!user.getProfilePublic() || !user.getStatsPublic()) {
            return UserStatsResponse.builder()
                    .rank(0)
                    .totalUsers(0)
                    .solvedCount(0)
                    .failedCount(0)
                    .totalSubmissions(0)
                    .acceptanceRate(0.0)
                    .build();
        }

        // 젬(포인트)은 다른 사용자에게 보여주지 않음
        UserStatsResponse stats = buildUserStats(user);
        return UserStatsResponse.builder()
                .rank(stats.getRank())
                .totalUsers(stats.getTotalUsers())
                .solvedCount(stats.getSolvedCount())
                .failedCount(stats.getFailedCount())
                .totalSubmissions(stats.getTotalSubmissions())
                .acceptanceRate(stats.getAcceptanceRate())
                .easyCount(stats.getEasyCount())
                .mediumCount(stats.getMediumCount())
                .hardCount(stats.getHardCount())
                .tier(stats.getTier())
                .tierLevel(stats.getTierLevel())
                .currentXP(stats.getCurrentXP())
                .xpToNextTier(stats.getXpToNextTier())
                .streak(user.getActivityPublic() ? stats.getStreak() : 0)
                .isStreakActive(user.getActivityPublic() ? stats.isStreakActive() : false)
                // 젬은 제외
                .problemPoints(0)
                .shopPoints(0)
                .build();
    }

    /**
     * 사용자 권한 변경 (ADMIN 전용).
     *
     * @param userId 대상 사용자 ID
     * @param role   새로운 권한
     * @return 변경된 사용자 정보
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, Role role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.updateRole(role);
        return UserResponse.from(user);
    }

    /**
     * 전체 사용자 목록 조회 (ADMIN 전용).
     *
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    /**
     * ID로 사용자 조회.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    public UserResponse getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return UserResponse.from(user);
    }
}
