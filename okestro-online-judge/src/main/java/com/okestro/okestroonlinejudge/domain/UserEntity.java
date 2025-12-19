package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity extends BaseTimeEntity {

    /**
     * 사용자 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 티어 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id")
    private TierEntity tierEntity;

    /**
     * 사용자의 소속 조직 정보 (이름 직접 입력)
     */
    @Column(name = "organization_name")
    private String organizationName;

    /**
     * 사용자의 소속 조직 정보 (엔티티 참조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organizationEntity;

    /**
     * 사용자명 (실명, 변경 불가)
     */
    @Column(nullable = false)
    private String username;

    /**
     * 닉네임 (게임 내 표시명, 변경 가능, 고유값)
     */
    @Column(nullable = false, unique = true)
    private String nickname;

    /**
     * 비밀번호 해시값
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 이메일 주소 (고유값)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image")
    private String profileImage;

    /**
     * 자기소개
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * 사용자 권한
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * TOTP 비밀키 (Google Authenticator용)
     */
    @Column(name = "totp_secret")
    private String totpSecret;

    // ==================== 프로필 공개 설정 ====================

    /**
     * 프로필 공개 여부 (기본값: true)
     */
    @Column(name = "profile_public", nullable = false)
    private Boolean profilePublic = true;

    /**
     * 통계 공개 여부 (정답률, 풀이 수 등)
     */
    @Column(name = "stats_public", nullable = false)
    private Boolean statsPublic = true;

    /**
     * 풀이 기록 공개 여부
     */
    @Column(name = "solved_problems_public", nullable = false)
    private Boolean solvedProblemsPublic = true;

    /**
     * 활동 기록 공개 여부 (스트릭, 연속 풀이 등)
     */
    @Column(name = "activity_public", nullable = false)
    private Boolean activityPublic = true;

    /**
     * 출제한 문제 수
     */
    @Column(name = "created_problems_count", nullable = false)
    private Integer createdProblemsCount = 0;

    /**
     * 공유한 솔루션 수
     */
    @Column(name = "shared_solutions_count", nullable = false)
    private Integer sharedSolutionsCount = 0;

    /**
     * User 생성자.
     *
     * @param username 사용자명
     * @param passwordHash 비밀번호 해시
     * @param email 이메일
     * @param role 권한
     * @param organizationEntity 조직
     * @param tierEntity 티어
     */
    public UserEntity(String username, String nickname, String passwordHash, String email, Role role, OrganizationEntity organizationEntity, TierEntity tierEntity) {
        this.username = username;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.organizationEntity = organizationEntity;
        this.tierEntity = tierEntity;
        initializeDefaults();
    }

    /**
     * User 생성자 (TOTP 포함).
     *
     * @param username 사용자명
     * @param nickname 닉네임
     * @param passwordHash 비밀번호 해시
     * @param email 이메일
     * @param role 권한
     * @param organizationEntity 조직
     * @param tierEntity 티어
     * @param totpSecret TOTP 비밀키
     */
    public UserEntity(String username, String nickname, String passwordHash, String email, Role role, OrganizationEntity organizationEntity, TierEntity tierEntity, String totpSecret) {
        this.username = username;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.organizationEntity = organizationEntity;
        this.tierEntity = tierEntity;
        this.totpSecret = totpSecret;
        initializeDefaults();
    }

    /**
     * 기본값 초기화 (프라이버시 설정, 카운터 등).
     */
    private void initializeDefaults() {
        this.profilePublic = true;
        this.statsPublic = true;
        this.solvedProblemsPublic = true;
        this.activityPublic = true;
        this.createdProblemsCount = 0;
        this.sharedSolutionsCount = 0;
    }

    /**
     * 티어 정보를 업데이트한다.
     *
     * @param tierEntity 변경할 티어
     */
    public void updateTier(TierEntity tierEntity) {
        this.tierEntity = tierEntity;
    }

    /**
     * 비밀번호를 변경한다.
     *
     * @param passwordHash 새 비밀번호 해시
     */
    public void updatePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * 닉네임을 변경한다.
     *
     * @param nickname 새 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 프로필 정보를 업데이트한다.
     *
     * @param username 사용자명
     * @param organizationName 조직 이름
     * @param bio 자기소개
     */
    public void updateProfile(String username, String organizationName, String bio) {
        this.username = username;
        this.organizationName = organizationName;
        this.bio = bio;
    }

    /**
     * 회원 탈퇴 처리를 수행한다 (익명화).
     *
     * @param username 익명화된 사용자명
     * @param email 익명화된 이메일
     * @param passwordHash 임의의 비밀번호 해시
     */
    public void withdraw(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.organizationName = null;
        this.organizationEntity = null;
        this.bio = null;
        this.profileImage = null;
        this.totpSecret = null;
    }

    /**
     * 프로필 이미지를 업데이트한다.
     *
     * @param profileImage 프로필 이미지 URL
     */
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * 프로필 공개 설정을 업데이트한다.
     */
    public void updatePrivacySettings(Boolean profilePublic, Boolean statsPublic,
                                      Boolean solvedProblemsPublic, Boolean activityPublic) {
        if (profilePublic != null) this.profilePublic = profilePublic;
        if (statsPublic != null) this.statsPublic = statsPublic;
        if (solvedProblemsPublic != null) this.solvedProblemsPublic = solvedProblemsPublic;
        if (activityPublic != null) this.activityPublic = activityPublic;
    }

    /**
     * 출제 문제 수를 증가시킨다.
     */
    public void incrementCreatedProblemsCount() {
        this.createdProblemsCount++;
    }

    /**
     * 공유 솔루션 수를 증가시킨다.
     */
    public void incrementSharedSolutionsCount() {
        this.sharedSolutionsCount++;
    }

    /**
     * 공유 솔루션 수를 감소시킨다.
     */
    public void decrementSharedSolutionsCount() {
        if (this.sharedSolutionsCount > 0) {
            this.sharedSolutionsCount--;
        }
    }

    /**
     * 소속 조직을 변경한다.
     *
     * @param organizationEntity 새로운 조직 (null이면 조직 해제)
     */
    public void updateOrganization(OrganizationEntity organizationEntity) {
        this.organizationEntity = organizationEntity;
    }

    /**
     * 사용자 권한을 변경한다.
     *
     * @param role 새로운 권한
     */
    public void updateRole(Role role) {
        this.role = role;
    }
}

