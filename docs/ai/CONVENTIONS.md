# 코딩 컨벤션

## 🎯 기본 원칙

- **일관성**: 프로젝트 전체에서 동일한 스타일 유지
- **가독성**: 명확하고 이해하기 쉬운 코드 작성
- **간결성**: 불필요한 복잡도 제거
- **Java 21 활용**: 최신 Java 기능 적극 활용

## 📝 네이밍 규칙

### 패키지명
- **소문자만 사용**: `com.okestro.okestroonlinejudge.domain`
- **복수형 지양**: `util` (O), `utils` (X)
- **의미 명확**: `dto.request`, `dto.response`, `dto.judge0`

### 클래스명
- **PascalCase**: 첫 글자 대문자
- **명사 사용**: `ProblemEntity`, `SubmissionService`
- **접미사 규칙**:
  - Entity: `{Resource}Entity` (예: `ProblemEntity`)
  - DTO: `{Action}{Resource}Request/Response` (예: `CreateProblemRequest`)
  - Service: `{Resource}Service` (예: `ProblemService`)
  - Repository: `{Resource}Repository`
  - Controller: `{Resource}Controller`
  - Config: `{Purpose}Config` (예: `SecurityConfig`)

### 메서드명
- **camelCase**: 첫 글자 소문자
- **동사로 시작**: `getProblem()`, `createSubmission()`, `validateTestCase()`
- **Boolean 메서드**: `is`, `has`, `can` 접두사 사용
  ```java
  public boolean isPublished() { }
  public boolean hasPermission() { }
  public boolean canSubmit() { }
  ```

### 변수명
- **camelCase**: `userId`, `problemTitle`
- **상수**: `UPPER_SNAKE_CASE` (예: `MAX_TIME_LIMIT`)
- **컬렉션**: 복수형 사용 (예: `problems`, `submissions`)
- **Boolean 변수**: `is`, `has`, `can` 접두사
  ```java
  private boolean isPublic;
  private boolean hasTestCases;
  ```

### 축약어 사용 규칙
- **일반적인 축약어는 허용**: `id`, `dto`, `url`, `api`
- **도메인 축약어는 주의**: `prob` (X) → `problem` (O)
- **축약어도 camelCase**: `userId` (O), `userID` (X)

## 🏗 코드 구조

### 클래스 멤버 순서

```java
public class ExampleClass {

    // 1. 상수 (static final)
    private static final int MAX_RETRY = 3;

    // 2. 클래스 변수 (static)
    private static int instanceCount;

    // 3. 인스턴스 변수
    private Long id;
    private String name;

    // 4. 생성자
    public ExampleClass() { }

    // 5. 정적 팩토리 메서드
    public static ExampleClass of(String name) { }

    // 6. 비즈니스 로직 메서드 (public)
    public void process() { }

    // 7. private 메서드
    private void validate() { }

    // 8. Getter/Setter (필요한 경우만)
}
```

### 메서드 길이
- **최대 30줄 이내**: 한 메서드는 한 가지 일만
- **중첩 깊이**: 3단계 이하 권장
- **Extract Method**: 긴 메서드는 여러 메서드로 분리

## 📦 Java 21 기능 활용

### Record 사용

**DTO에 적극 활용:**
```java
// 간단한 Response DTO는 Record 사용
public record ProblemSummaryResponse(
        Long id,
        String title,
        Integer tierId,
        Double acceptanceRate
) {
    public static ProblemSummaryResponse from(ProblemEntity problem) {
        return new ProblemSummaryResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getTier().getId(),
                problem.getStatistics().getAcceptanceRate()
        );
    }
}
```

**사용하면 안 되는 경우:**
- 상속이 필요한 경우
- 빌더 패턴이 필요한 복잡한 DTO
- 기본값이 필요한 필드가 있는 경우

### Pattern Matching (Switch Expressions)

```java
public String getStatusMessage(SubmissionResult result) {
    return switch (result) {
        case ACCEPTED -> "정답입니다!";
        case WRONG_ANSWER -> "오답입니다.";
        case TIME_LIMIT_EXCEEDED -> "시간 초과";
        case MEMORY_LIMIT_EXCEEDED -> "메모리 초과";
        case RUNTIME_ERROR -> "런타임 에러";
        case COMPILE_ERROR -> "컴파일 에러";
    };
}
```

### Text Blocks

**긴 문자열은 Text Block 사용:**
```java
String sqlQuery = """
        SELECT p.id, p.title, COUNT(s.id) as submission_count
        FROM problem p
        LEFT JOIN submission s ON p.id = s.problem_id
        WHERE p.status = 'PUBLISHED'
        GROUP BY p.id
        ORDER BY submission_count DESC
        """;
```

### Virtual Threads (필요 시)

```java
@Configuration
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

### Sealed Classes (도메인 모델링)

```java
public sealed interface JudgeResult
        permits Accepted, WrongAnswer, RuntimeError {

    record Accepted(int executionTime, int memoryUsed) implements JudgeResult {}
    record WrongAnswer(String expectedOutput, String actualOutput) implements JudgeResult {}
    record RuntimeError(String errorMessage) implements JudgeResult {}
}
```

## 🎨 포매팅

### 들여쓰기
- **4 스페이스** (탭 사용 금지)
- **IntelliJ IDEA 기본 설정 사용**

### 중괄호
- **K&R 스타일** (같은 줄에 여는 괄호)
```java
public void method() {
    if (condition) {
        // code
    } else {
        // code
    }
}
```

### 줄 길이
- **최대 120자** (IntelliJ 기본)
- 긴 줄은 논리적 단위로 분리

### 공백
```java
// 연산자 앞뒤 공백
int sum = a + b;

// 쉼표 뒤 공백
method(arg1, arg2, arg3);

// 키워드와 괄호 사이 공백
if (condition) { }
for (int i = 0; i < 10; i++) { }

// 메서드 호출 시 괄호 앞 공백 없음
callMethod();
```

## 📝 주석 작성

### JavaDoc
- **Public API는 필수**: 모든 public 클래스, 메서드
- **Package-private 이하는 선택**: 복잡한 로직만

```java
/**
 * 문제를 생성합니다.
 *
 * @param request 문제 생성 요청 DTO
 * @return 생성된 문제 정보
 * @throws DuplicateProblemException 동일한 제목의 문제가 이미 존재하는 경우
 */
public ProblemDetailResponse createProblem(CreateProblemRequest request) {
    // 구현
}
```

### 인라인 주석
- **Why, not What**: 코드가 무엇을 하는지가 아닌, 왜 하는지 설명
- **불필요한 주석 금지**: 코드로 충분히 설명되는 내용은 주석 불필요

```java
// Bad: 무엇을 하는지 설명
// 사용자 ID로 사용자를 찾는다
User user = userRepository.findById(userId);

// Good: 왜 하는지 설명
// Judge0 API는 30초 타임아웃이 있어 비동기 처리 필요
CompletableFuture.supplyAsync(() -> judge0Client.submit(code));
```

### TODO 주석
```java
// TODO(작성자): 추후 캐싱 적용 필요
// TODO(홍길동): Judge0 응답 시간 모니터링 추가 (2025-12-10까지)
```

## 🔧 어노테이션

### 순서
```java
@Entity
@Table(name = "problem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemEntity {
    // ...
}
```

### Lombok 사용 규칙
- **권장**: `@Getter`, `@Builder`, `@NoArgsConstructor`
- **주의**: `@Data`, `@AllArgsConstructor` (JPA Entity에서 사용 금지)
- **금지**: `@Setter` (Entity에서 사용 금지, 명시적 메서드 사용)

```java
// Good: 명시적 메서드
public class ProblemEntity {
    @Getter
    private String title;

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }
}

// Bad: Setter 사용
@Setter
private String title; // Entity에서 금지
```

## ✅ 예외 처리

### 체크 예외 vs 언체크 예외
- **비즈니스 예외**: 커스텀 RuntimeException 사용
- **복구 불가능**: RuntimeException
- **복구 가능**: 체크 예외 (드물게 사용)

### 커스텀 예외
```java
public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(Long problemId) {
        super("문제를 찾을 수 없습니다. ID: " + problemId);
    }
}
```

### 예외 처리
```java
// Good: 구체적인 예외 처리
try {
    return judge0Client.submit(code);
} catch (Judge0ApiException e) {
    log.error("Judge0 API 호출 실패: {}", e.getMessage());
    throw new SubmissionFailedException("채점 요청 실패", e);
}

// Bad: 모든 예외를 catch
try {
    // ...
} catch (Exception e) { // 너무 광범위
    // ...
}
```

## 🧪 테스트 코드

### 테스트 메서드 네이밍
```java
@Test
void createProblem_ValidRequest_Success() {
    // Given-When-Then
}

@Test
void createProblem_DuplicateTitle_ThrowsException() {
    // ...
}
```

### Given-When-Then 패턴
```java
@Test
void submitCode_AcceptedCase_ReturnsAcceptedResult() {
    // Given: 준비
    Long problemId = 1L;
    SubmitCodeRequest request = createValidRequest();

    // When: 실행
    SubmissionResponse response = submissionService.submit(problemId, request);

    // Then: 검증
    assertThat(response.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
}
```

## ✅ 체크리스트

코드 작성 시 다음 사항을 확인하세요:

- [ ] 네이밍 규칙 준수 (PascalCase, camelCase)
- [ ] 메서드 길이 30줄 이내
- [ ] Java 21 기능 활용 (Record, Switch Expression 등)
- [ ] Public API에 JavaDoc 작성
- [ ] Lombok 어노테이션 적절히 사용
- [ ] Entity에 Setter 사용하지 않음
- [ ] 커스텀 예외 사용
- [ ] 테스트 코드 작성 (Given-When-Then)

## 📝 변경 이력

- 2025-12-05: 코딩 컨벤션 문서 작성
