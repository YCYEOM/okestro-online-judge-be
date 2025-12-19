# 데이터베이스 가이드

## 🎯 데이터베이스 설계 원칙

- **ERD 기반 설계**: 먼저 ERD 설계 후 Entity 생성
- **정규화**: 3차 정규형(3NF)까지 정규화
- **연관관계 명확화**: 양방향/단방향 관계 명확히 정의
- **성능 고려**: 인덱스, N+1 문제 해결

## 📊 ERD 개요

### 핵심 엔티티

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Organization │       │     User     │       │     Tier     │
└──────────────┘       └──────────────┘       └──────────────┘
        │                      │                      │
        │                      │                      │
        │                      ▼                      ▼
        │              ┌──────────────┐       ┌──────────────┐
        └─────────────→│   Problem    │◄──────│  ProblemTag  │
                       └──────────────┘       └──────────────┘
                              │                       ▲
                              │                       │
                       ┌──────┴──────┐               │
                       │              │               │
                       ▼              ▼               │
               ┌──────────────┐ ┌──────────────┐     │
               │  TestCase    │ │  Submission  │     │
               └──────────────┘ └──────────────┘     │
                                       │              │
                                       ▼              │
                               ┌──────────────┐      │
                               │ UserProblem  │──────┘
                               │   Status     │
                               └──────────────┘
```

## 🗂 Entity 설계 가이드

### BaseTimeEntity (공통 필드)

모든 Entity는 `BaseTimeEntity`를 상속받습니다.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### Entity 작성 규칙

```java
@Entity
@Table(
    name = "problem",
    indexes = {
        @Index(name = "idx_problem_tier", columnList = "tier_id"),
        @Index(name = "idx_problem_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemEntity extends BaseTimeEntity {

    // 1. 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 일반 필드
    @Column(nullable = false, length = 200, unique = true)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // 3. 연관관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private TierEntity tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    // 4. Enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProblemStatus status = ProblemStatus.DRAFT;

    // 5. 일대다 (양방향인 경우만)
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCaseEntity> testCases = new ArrayList<>();

    // 6. 빌더
    @Builder
    public ProblemEntity(String title, String description, TierEntity tier,
                         OrganizationEntity organization, Integer timeLimit,
                         Integer memoryLimit) {
        this.title = title;
        this.description = description;
        this.tier = tier;
        this.organization = organization;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    // 7. 비즈니스 메서드
    public void updateInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void publish() {
        if (testCases.isEmpty()) {
            throw new CannotPublishException("테스트케이스가 없는 문제는 공개할 수 없습니다");
        }
        this.status = ProblemStatus.PUBLISHED;
    }
}
```

## 🔗 연관관계 매핑

### 단방향 vs 양방향

**단방향 (권장):**
```java
// Submission → Problem (다대일)
@Entity
public class SubmissionEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;
}
```

**양방향 (필요한 경우만):**
```java
// Problem ↔ TestCase
@Entity
public class ProblemEntity {
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCaseEntity> testCases = new ArrayList<>();

    // 편의 메서드
    public void addTestCase(TestCaseEntity testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }
}

@Entity
public class TestCaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    // 양방향 설정용 (package-private)
    void setProblem(ProblemEntity problem) {
        this.problem = problem;
    }
}
```

### FetchType 규칙
- **기본값**: `LAZY` (지연 로딩)
- **예외**: `@ManyToOne`, `@OneToOne`도 명시적으로 `LAZY` 설정

```java
// ✅ Good
@ManyToOne(fetch = FetchType.LAZY)
private ProblemEntity problem;

// ❌ Bad (N+1 문제 발생 가능)
@ManyToOne(fetch = FetchType.EAGER)
private ProblemEntity problem;
```

### Cascade 타입
- **ALL**: 부모-자식 생명주기 동일 (예: Problem-TestCase)
- **PERSIST**: 저장만 전파
- **REMOVE**: 삭제만 전파
- **주의**: REMOVE는 신중히 사용

```java
// 부모가 삭제되면 자식도 함께 삭제
@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
private List<TestCaseEntity> testCases;
```

## 📋 주요 엔티티 설계

### ProblemEntity (문제)

```java
@Entity
@Table(name = "problem")
public class ProblemEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private TierEntity tier;

    @Column(nullable = false)
    private Integer timeLimit = 2000; // ms

    @Column(nullable = false)
    private Integer memoryLimit = 256; // MB

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProblemStatus status = ProblemStatus.DRAFT;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCaseEntity> testCases = new ArrayList<>();

    @OneToOne(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProblemStatisticsEntity statistics;
}
```

### SubmissionEntity (제출)

```java
@Entity
@Table(
    name = "submission",
    indexes = {
        @Index(name = "idx_submission_user", columnList = "user_id"),
        @Index(name = "idx_submission_problem", columnList = "problem_id"),
        @Index(name = "idx_submission_result", columnList = "result")
    }
)
public class SubmissionEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Column(nullable = false, length = 50)
    private String language; // "java", "python", "cpp"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionResult result;

    @Column
    private Integer executionTime; // ms

    @Column
    private Integer memoryUsed; // KB

    @Column(columnDefinition = "TEXT")
    private String errorMessage; // 컴파일 에러, 런타임 에러 메시지
}
```

### TestCaseEntity (테스트케이스)

```java
@Entity
@Table(name = "test_case")
public class TestCaseEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(nullable = false)
    private Boolean isPublic = false; // 공개 여부 (예제 입출력)

    @Column(nullable = false)
    private Integer orderIndex = 0; // 실행 순서

    @Column(nullable = false)
    private Integer score = 10; // 부분 점수
}
```

## 🔍 쿼리 최적화

### N+1 문제 해결

**문제 상황:**
```java
// N+1 발생!
List<ProblemEntity> problems = problemRepository.findAll();
for (ProblemEntity problem : problems) {
    String tierName = problem.getTier().getName(); // 각 문제마다 쿼리 발생
}
```

**해결 방법 1: Fetch Join**
```java
@Query("SELECT p FROM ProblemEntity p " +
       "LEFT JOIN FETCH p.tier " +
       "LEFT JOIN FETCH p.statistics")
List<ProblemEntity> findAllWithTierAndStatistics();
```

**해결 방법 2: EntityGraph**
```java
@EntityGraph(attributePaths = {"tier", "statistics"})
List<ProblemEntity> findAll();
```

### 페이징 쿼리

```java
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query("SELECT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.tier " +
           "WHERE p.status = :status")
    Page<ProblemEntity> findByStatus(@Param("status") ProblemStatus status, Pageable pageable);
}
```

### 통계 쿼리

```java
@Query("SELECT new com.okestro.dto.ProblemStatisticsDto(" +
       "p.id, p.title, COUNT(s), " +
       "SUM(CASE WHEN s.result = 'ACCEPTED' THEN 1 ELSE 0 END)) " +
       "FROM ProblemEntity p " +
       "LEFT JOIN SubmissionEntity s ON p.id = s.problem.id " +
       "GROUP BY p.id")
List<ProblemStatisticsDto> getProblemStatistics();
```

## 🏷 인덱스 설정

### 인덱스 추가 기준
1. **WHERE 절에 자주 사용**: `status`, `userId`, `problemId`
2. **JOIN 컬럼**: 외래키
3. **ORDER BY 컬럼**: `createdAt`, `score`
4. **복합 인덱스**: 자주 함께 조회되는 컬럼

```java
@Table(
    name = "submission",
    indexes = {
        @Index(name = "idx_submission_user_problem", columnList = "user_id, problem_id"),
        @Index(name = "idx_submission_created", columnList = "created_at DESC")
    }
)
```

### 인덱스 주의사항
- **과도한 인덱스 금지**: INSERT/UPDATE 성능 저하
- **카디널리티 고려**: 값의 종류가 많은 컬럼에 인덱스
- **복합 인덱스 순서**: 선택도가 높은 컬럼 우선

## 🔐 데이터 무결성

### NOT NULL 제약
```java
@Column(nullable = false)
private String title;
```

### UNIQUE 제약
```java
@Column(nullable = false, unique = true)
private String title;
```

### CHECK 제약 (코드 레벨)
```java
public void setTimeLimit(Integer timeLimit) {
    if (timeLimit < 1 || timeLimit > 10000) {
        throw new InvalidTimeLimitException(timeLimit);
    }
    this.timeLimit = timeLimit;
}
```

## 🎨 Enum 매핑

### String vs Ordinal

```java
// ✅ Good: EnumType.STRING (권장)
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 30)
private SubmissionResult result;

// ❌ Bad: EnumType.ORDINAL (금지)
@Enumerated(EnumType.ORDINAL) // Enum 순서 변경 시 데이터 오염
private SubmissionResult result;
```

### Enum 정의
```java
public enum SubmissionResult {
    PENDING,
    JUDGING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILE_ERROR,
    SYSTEM_ERROR
}
```

## ✅ 체크리스트

Entity 추가 시 확인 사항:

- [ ] `BaseTimeEntity` 상속
- [ ] `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- [ ] Setter 사용하지 않음
- [ ] FetchType.LAZY 명시
- [ ] Enum은 EnumType.STRING
- [ ] 인덱스 설정 (@Index)
- [ ] NOT NULL, UNIQUE 제약 설정
- [ ] 빌더 패턴 사용
- [ ] 비즈니스 메서드 작성
- [ ] N+1 문제 해결 (Fetch Join, EntityGraph)

## 📝 변경 이력

- 2025-12-05: 데이터베이스 가이드 문서 작성
