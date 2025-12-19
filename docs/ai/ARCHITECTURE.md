# 아키텍처 가이드

## 🏗 레이어 아키텍처

우리 프로젝트는 **레이어드 아키텍처**를 따릅니다.

```
┌─────────────────────────────────────┐
│     Presentation Layer (Controller) │  ← HTTP 요청/응답 처리
├─────────────────────────────────────┤
│     Application Layer (Service)     │  ← 비즈니스 로직
├─────────────────────────────────────┤
│     Domain Layer (Entity/Domain)    │  ← 도메인 모델
├─────────────────────────────────────┤
│  Infrastructure Layer (Repository)  │  ← 데이터 접근
└─────────────────────────────────────┘
         ↓                 ↓
    Database          External API
                      (Judge0, MinIO)
```

### 각 레이어의 책임

#### 1. Presentation Layer (Controller)
- **위치**: `controller` 패키지
- **책임**:
  - HTTP 요청 수신 및 파라미터 검증
  - Service 호출
  - HTTP 응답 생성
- **금지 사항**:
  - 비즈니스 로직 포함
  - Repository 직접 호출
  - Entity 직접 반환

```java
@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemDetailResponse> createProblem(
            @Valid @RequestBody CreateProblemRequest request) {

        ProblemDetailResponse response = problemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

#### 2. Application Layer (Service)
- **위치**: `service` 패키지
- **책임**:
  - 비즈니스 로직 구현
  - 트랜잭션 관리 (`@Transactional`)
  - Entity ↔ DTO 변환
  - 외부 API 호출 조율
- **금지 사항**:
  - HTTP 관련 객체 사용 (HttpServletRequest 등)
  - 직접 SQL 작성

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TierRepository tierRepository;

    @Transactional
    public ProblemDetailResponse create(CreateProblemRequest request) {
        // 1. 비즈니스 검증
        validateDuplicateTitle(request.getTitle());

        // 2. Entity 생성
        TierEntity tier = tierRepository.findById(request.getTierId())
            .orElseThrow(() -> new TierNotFoundException(request.getTierId()));

        ProblemEntity problem = ProblemEntity.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .tier(tier)
            .timeLimit(request.getTimeLimit())
            .memoryLimit(request.getMemoryLimit())
            .build();

        // 3. 저장
        ProblemEntity saved = problemRepository.save(problem);

        // 4. DTO 변환
        return ProblemDetailResponse.from(saved);
    }

    private void validateDuplicateTitle(String title) {
        if (problemRepository.existsByTitle(title)) {
            throw new DuplicateProblemTitleException(title);
        }
    }
}
```

#### 3. Domain Layer (Entity)
- **위치**: `domain` 패키지
- **책임**:
  - 도메인 개념 표현
  - 도메인 규칙 캡슐화
  - 연관관계 관리
- **금지 사항**:
  - Setter 사용 (명시적 메서드로 대체)
  - 비즈니스 로직을 Service로 누수

```java
@Entity
@Table(name = "problem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private TierEntity tier;

    @Column(nullable = false)
    private Integer timeLimit = 2000;

    @Column(nullable = false)
    private Integer memoryLimit = 256;

    @Builder
    public ProblemEntity(String title, String description, TierEntity tier,
                         Integer timeLimit, Integer memoryLimit) {
        this.title = title;
        this.description = description;
        this.tier = tier;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    // 비즈니스 메서드
    public void updateInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateLimits(Integer timeLimit, Integer memoryLimit) {
        validateTimeLimit(timeLimit);
        validateMemoryLimit(memoryLimit);
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    private void validateTimeLimit(Integer timeLimit) {
        if (timeLimit < 1 || timeLimit > 10000) {
            throw new InvalidTimeLimitException(timeLimit);
        }
    }

    private void validateMemoryLimit(Integer memoryLimit) {
        if (memoryLimit < 1 || memoryLimit > 1024) {
            throw new InvalidMemoryLimitException(memoryLimit);
        }
    }
}
```

#### 4. Infrastructure Layer (Repository)
- **위치**: `repository` 패키지
- **책임**:
  - 데이터베이스 접근
  - 쿼리 메서드 정의
- **금지 사항**:
  - 비즈니스 로직 포함

```java
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    boolean existsByTitle(String title);

    List<ProblemEntity> findByTier(TierEntity tier);

    @Query("SELECT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.tier " +
           "WHERE p.id = :id")
    Optional<ProblemEntity> findByIdWithTier(@Param("id") Long id);
}
```

## 📦 패키지 구조

```
com.okestro.okestroonlinejudge
├── config                      # 설정 클래스
│   ├── JpaAuditingConfig
│   ├── SecurityConfig
│   ├── SwaggerConfig
│   ├── MinioConfig
│   └── Judge0Properties
│
├── controller                  # Presentation Layer
│   ├── ProblemController
│   ├── SubmissionController
│   └── TestCaseController
│
├── service                     # Application Layer
│   ├── ProblemService
│   ├── SubmissionService
│   ├── TestCaseService
│   ├── UserService
│   └── StorageService
│       └── MinioStorageService
│
├── domain                      # Domain Layer
│   ├── ProblemEntity
│   ├── SubmissionEntity
│   ├── TestCaseEntity
│   ├── UserEntity
│   ├── TierEntity
│   ├── ProblemStatus (enum)
│   ├── SubmissionResult (enum)
│   ├── Role (enum)
│   └── Judge0LanguageId (enum)
│
├── repository                  # Infrastructure Layer
│   ├── ProblemRepository
│   ├── SubmissionRepository
│   ├── TestCaseRepository
│   └── UserRepository
│
├── dto                         # Data Transfer Objects
│   ├── request
│   │   ├── CreateProblemRequest
│   │   ├── SubmitCodeRequest
│   │   └── PageRequestDto
│   ├── response
│   │   ├── ProblemDetailResponse
│   │   ├── SubmissionResponse
│   │   ├── PageResponse
│   │   └── ErrorResponse
│   └── judge0
│       ├── Judge0SubmissionRequest
│       └── Judge0SubmissionResponse
│
├── exception                   # 커스텀 예외
│   ├── ProblemNotFoundException
│   ├── SubmissionFailedException
│   └── GlobalExceptionHandler
│
└── util                        # 유틸리티 클래스
    └── (필요 시 추가)
```

## 🔄 의존성 방향

### 의존성 규칙
- **하위 레이어만 의존**: 상위 레이어는 하위 레이어만 의존 가능
- **같은 레이어 의존 가능**: 같은 레이어 내 클래스 간 의존 허용
- **순환 의존 금지**: 어떤 경우에도 순환 의존 금지

```
Controller → Service → Repository
    ↓           ↓           ↓
   DTO       Entity      Entity
```

### 잘못된 의존성 예시
```java
// ❌ Bad: Repository가 Service 의존
public class ProblemRepository {
    private ProblemService problemService; // 금지!
}

// ❌ Bad: Entity가 Controller 의존
@Entity
public class ProblemEntity {
    private ProblemController controller; // 금지!
}

// ❌ Bad: Entity를 Controller에서 직접 반환
@GetMapping("/{id}")
public ProblemEntity getProblem(@PathVariable Long id) { // 금지!
    return problemService.findById(id);
}
```

### 올바른 의존성 예시
```java
// ✅ Good: Controller → Service → Repository
@RestController
public class ProblemController {
    private final ProblemService problemService;

    public ResponseEntity<ProblemDetailResponse> getProblem(Long id) {
        return ResponseEntity.ok(problemService.findById(id));
    }
}

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public ProblemDetailResponse findById(Long id) {
        ProblemEntity problem = problemRepository.findById(id)
            .orElseThrow(() -> new ProblemNotFoundException(id));
        return ProblemDetailResponse.from(problem);
    }
}
```

## 🎯 트랜잭션 관리

### 트랜잭션 범위
- **Service Layer에서만**: `@Transactional`은 Service에서만 사용
- **readOnly 최적화**: 조회 메서드는 `@Transactional(readOnly = true)`

```java
@Service
@Transactional(readOnly = true) // 클래스 레벨: 기본 readOnly
public class ProblemService {

    @Transactional // 메서드 레벨: 쓰기 트랜잭션
    public ProblemDetailResponse create(CreateProblemRequest request) {
        // 생성 로직
    }

    // readOnly는 클래스 레벨에서 상속
    public ProblemDetailResponse findById(Long id) {
        // 조회 로직
    }
}
```

### 트랜잭션 전파
- **기본값 사용**: `REQUIRED` (기본값)
- **특수한 경우만 명시**: `REQUIRES_NEW`, `NOT_SUPPORTED` 등

## 🔌 외부 연동

### Judge0 연동
```java
@Service
@RequiredArgsConstructor
public class Judge0Client {

    private final RestTemplate restTemplate;
    private final Judge0Properties judge0Properties;

    public Judge0Response submit(Judge0SubmissionRequest request) {
        String url = judge0Properties.getUrl() + "/submissions";
        return restTemplate.postForObject(url, request, Judge0Response.class);
    }
}
```

### MinIO 연동
```java
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;

    @Override
    public String uploadTestCase(Long problemId, MultipartFile file) {
        // MinIO 업로드 로직
    }
}
```

## ✅ 아키텍처 체크리스트

새로운 기능 추가 시 확인:

- [ ] Controller는 Service만 호출
- [ ] Service에 비즈니스 로직 집중
- [ ] Entity에 Setter 사용하지 않음
- [ ] DTO로 Entity 직접 노출 방지
- [ ] 트랜잭션은 Service Layer에서만
- [ ] 패키지 구조 준수
- [ ] 순환 의존 없음
- [ ] 레이어 간 의존성 방향 준수

## 📝 변경 이력

- 2025-12-05: 아키텍처 가이드 문서 작성
