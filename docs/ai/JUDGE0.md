# Judge0 커스터마이징 가이드

## 🎯 Judge0 개요

Judge0는 코드 실행 및 채점을 위한 오픈소스 API 서비스입니다.

### Judge0의 역할
- **샌드박스 환경**: 안전한 코드 실행 환경 제공
- **다중 언어 지원**: 60+ 프로그래밍 언어 지원
- **리소스 제한**: CPU 시간, 메모리 제한 설정
- **컴파일 및 실행**: 컴파일, 실행, 결과 수집 자동화

### 우리 프로젝트의 활용 범위
- **Judge0 사용**: 코드 실행 엔진으로만 활용
- **자체 구현**: 테스트케이스 관리, 결과 비교, 통계 집계

## 📋 지원 언어 목록

`Judge0LanguageId` enum에 정의된 13개 언어:

| 언어 | Judge0 ID | 컴파일러/인터프리터 |
|------|-----------|---------------------|
| Bash | 46 | GNU Bash 5.0.0 |
| C | 50 | GCC 9.2.0 |
| C++ | 54 | GCC 9.2.0 (C++17) |
| C# | 51 | Mono 6.6.0 |
| Go | 60 | Go 1.13.5 |
| Java | 62 | OpenJDK 13.0.1 |
| JavaScript | 63 | Node.js 12.14.0 |
| Kotlin | 78 | Kotlin 1.3.70 |
| Python3 | 71 | Python 3.8.1 |
| Ruby | 72 | Ruby 2.7.0 |
| Rust | 73 | Rust 1.40.0 |
| Swift | 83 | Swift 5.2.3 |
| TypeScript | 74 | TypeScript 3.7.4 |

> **참고**: Judge0 버전에 따라 언어 ID가 다를 수 있습니다. 공식 문서 확인 필수.

## 🔧 Judge0 설정

### application.yml 설정

```yaml
judge0:
  url: http://localhost:2358  # Judge0 API URL
  auth-token: ${JUDGE0_AUTH_TOKEN:}  # 인증 토큰 (선택)
  wait-timeout: 30  # 동기 채점 대기 시간 (초)
  polling-interval: 1000  # 비동기 폴링 간격 (ms)
  max-polling-attempts: 60  # 최대 폴링 횟수
```

### Judge0Properties 클래스

`Judge0Properties.java` 파일에서 설정 관리:

```java
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "judge0")
public class Judge0Properties {
    private String url;
    private String authToken;
    private Integer waitTimeout = 30;
    private Integer pollingInterval = 1000;
    private Integer maxPollingAttempts = 60;
}
```

## 📡 Judge0 API 연동

### 1. Submission 생성 (동기 방식)

**요청:**
```java
public record Judge0SubmissionRequest(
        String sourceCode,
        Integer languageId,
        String stdin,
        Integer cpuTimeLimit,  // 초 단위 (예: 2초)
        Integer memoryLimit    // KB 단위 (예: 256000 = 256MB)
) {}
```

**API 호출:**
```http
POST {judge0.url}/submissions?base64_encoded=false&wait=true
Content-Type: application/json

{
  "source_code": "print('Hello World')",
  "language_id": 71,
  "stdin": "",
  "cpu_time_limit": 2,
  "memory_limit": 256000
}
```

**응답:**
```json
{
  "token": "d85cd024-1548-4165-96c7-7bc88673f194",
  "status": {
    "id": 3,
    "description": "Accepted"
  },
  "time": "0.001",
  "memory": 2048,
  "stdout": "Hello World\n",
  "stderr": null,
  "compile_output": null
}
```

### 2. Submission 생성 (비동기 방식)

**요청:**
```http
POST {judge0.url}/submissions?base64_encoded=false&wait=false
```

**응답:**
```json
{
  "token": "d85cd024-1548-4165-96c7-7bc88673f194"
}
```

**결과 조회:**
```http
GET {judge0.url}/submissions/{token}?base64_encoded=false
```

### 3. Status ID 매핑

| Status ID | 설명 | 우리 시스템 매핑 |
|-----------|------|------------------|
| 1 | In Queue | PENDING |
| 2 | Processing | JUDGING |
| 3 | Accepted | ACCEPTED |
| 4 | Wrong Answer | WRONG_ANSWER |
| 5 | Time Limit Exceeded | TIME_LIMIT_EXCEEDED |
| 6 | Compilation Error | COMPILE_ERROR |
| 7 | Runtime Error (SIGSEGV) | RUNTIME_ERROR |
| 8 | Runtime Error (SIGXFSZ) | RUNTIME_ERROR |
| 9 | Runtime Error (SIGFPE) | RUNTIME_ERROR |
| 10 | Runtime Error (SIGABRT) | RUNTIME_ERROR |
| 11 | Runtime Error (NZEC) | RUNTIME_ERROR |
| 12 | Runtime Error (Other) | RUNTIME_ERROR |
| 13 | Internal Error | SYSTEM_ERROR |
| 14 | Exec Format Error | SYSTEM_ERROR |

## 🏗 커스텀 채점 로직

### 전체 채점 흐름

```
1. 사용자 코드 제출
   ↓
2. 테스트케이스 로드 (DB에서 조회)
   ↓
3. 각 테스트케이스마다 Judge0 호출
   ↓
4. Judge0 결과 수신
   ↓
5. 정답 비교 (자체 로직)
   ↓
6. 결과 저장 및 통계 업데이트
```

### 테스트케이스 실행 전략

**방식 1: 순차 실행**
```java
public SubmissionResult judgeSubmission(Long problemId, String sourceCode, String language) {
    List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);

    for (TestCase testCase : testCases) {
        Judge0SubmissionRequest request = new Judge0SubmissionRequest(
            sourceCode,
            Judge0LanguageId.fromLanguage(language).getId(),
            testCase.getInput(),
            problem.getTimeLimit(),
            problem.getMemoryLimit()
        );

        Judge0Response response = judge0Client.submit(request);

        // 하나라도 실패하면 즉시 종료 (단축 평가)
        if (!isCorrect(response.getStdout(), testCase.getExpectedOutput())) {
            return SubmissionResult.WRONG_ANSWER;
        }
    }

    return SubmissionResult.ACCEPTED;
}
```

**방식 2: 병렬 실행 (선택적)**
```java
public SubmissionResult judgeSubmissionParallel(Long problemId, String sourceCode, String language) {
    List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);

    List<CompletableFuture<Judge0Response>> futures = testCases.stream()
        .map(testCase -> CompletableFuture.supplyAsync(() ->
            judge0Client.submit(createRequest(sourceCode, language, testCase))
        ))
        .toList();

    // 모든 결과 대기
    List<Judge0Response> responses = futures.stream()
        .map(CompletableFuture::join)
        .toList();

    // 결과 검증
    // ...
}
```

### 정답 비교 로직

**엄격한 비교:**
```java
private boolean isExactMatch(String actual, String expected) {
    return actual.equals(expected);
}
```

**공백 무시 비교:**
```java
private boolean isCorrectIgnoringWhitespace(String actual, String expected) {
    String normalizedActual = actual.trim().replaceAll("\\s+", " ");
    String normalizedExpected = expected.trim().replaceAll("\\s+", " ");
    return normalizedActual.equals(normalizedExpected);
}
```

**줄 단위 비교:**
```java
private boolean isCorrectLineByLine(String actual, String expected) {
    String[] actualLines = actual.split("\n");
    String[] expectedLines = expected.split("\n");

    if (actualLines.length != expectedLines.length) {
        return false;
    }

    for (int i = 0; i < actualLines.length; i++) {
        if (!actualLines[i].trim().equals(expectedLines[i].trim())) {
            return false;
        }
    }

    return true;
}
```

## 🔐 보안 고려사항

### 1. 샌드박스 격리
- Judge0는 자체적으로 샌드박스 환경 제공
- 파일 시스템 접근 제한
- 네트워크 접근 차단

### 2. 리소스 제한
```java
// 문제별 제한 설정
private static final int MAX_TIME_LIMIT = 10000; // 10초
private static final int MAX_MEMORY_LIMIT = 1024000; // 1GB

public void validateLimits(CreateProblemRequest request) {
    if (request.getTimeLimit() > MAX_TIME_LIMIT) {
        throw new InvalidLimitException("시간 제한 초과");
    }
    if (request.getMemoryLimit() > MAX_MEMORY_LIMIT) {
        throw new InvalidLimitException("메모리 제한 초과");
    }
}
```

### 3. 코드 크기 제한
```java
private static final int MAX_CODE_LENGTH = 65536; // 64KB

public void validateCodeLength(String sourceCode) {
    if (sourceCode.length() > MAX_CODE_LENGTH) {
        throw new CodeTooLongException("코드 크기가 64KB를 초과합니다");
    }
}
```

## 📊 에러 처리

### Judge0 API 에러

```java
public Judge0Response submit(Judge0SubmissionRequest request) {
    try {
        return restTemplate.postForObject(
            judge0Url + "/submissions",
            request,
            Judge0Response.class
        );
    } catch (HttpClientErrorException e) {
        // 4xx 에러
        throw new Judge0ClientException("Judge0 요청 실패: " + e.getMessage());
    } catch (HttpServerErrorException e) {
        // 5xx 에러
        throw new Judge0ServerException("Judge0 서버 오류: " + e.getMessage());
    } catch (ResourceAccessException e) {
        // 타임아웃, 연결 실패
        throw new Judge0ConnectionException("Judge0 연결 실패", e);
    }
}
```

### 재시도 로직

```java
@Retryable(
    value = {Judge0ConnectionException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000)
)
public Judge0Response submitWithRetry(Judge0SubmissionRequest request) {
    return judge0Client.submit(request);
}
```

## ✅ 체크리스트

Judge0 관련 작업 시 확인 사항:

- [ ] Judge0Properties 설정 확인
- [ ] 지원 언어 ID 확인 (`Judge0LanguageId` enum)
- [ ] 시간/메모리 제한 단위 확인 (초, KB)
- [ ] Status ID 매핑 확인
- [ ] 정답 비교 로직 선택 (엄격/공백 무시)
- [ ] 에러 처리 및 재시도 로직 구현
- [ ] 리소스 제한 검증

## 📝 참고 자료

- [Judge0 공식 문서](https://ce.judge0.com/)
- [Judge0 GitHub](https://github.com/judge0/judge0)
- [Judge0 Language IDs](https://ce.judge0.com/#statuses-and-languages-language)

## 📝 변경 이력

- 2025-12-05: Judge0 커스터마이징 가이드 작성
