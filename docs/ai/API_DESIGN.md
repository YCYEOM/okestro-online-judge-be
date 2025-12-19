# API 설계 원칙

## 🎯 기본 원칙

### RESTful API 설계
- **리소스 중심**: URL은 리소스를 나타내고, HTTP 메서드로 행위를 표현
- **명사 사용**: URL에는 동사 대신 명사 사용 (`/problems`, `/submissions`)
- **계층 구조**: 리소스 간 관계를 URL로 표현 (`/problems/{id}/testcases`)
- **복수형**: 컬렉션은 복수형 사용 (`/problems`, `/users`)

### HTTP 메서드 사용
- `GET`: 조회 (단건 조회, 목록 조회)
- `POST`: 생성
- `PUT`: 전체 수정
- `PATCH`: 부분 수정
- `DELETE`: 삭제

## 📦 Request DTO 패턴

### 네이밍 규칙
- **생성 요청**: `Create{Resource}Request`
- **수정 요청**: `Update{Resource}Request`
- **제출 요청**: `Submit{Resource}Request`
- **검색 요청**: `Search{Resource}Request`

### Request DTO 작성 규칙

```java
package com.okestro.okestroonlinejudge.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 생성 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class CreateProblemRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    private String title;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

    @NotNull(message = "난이도는 필수입니다")
    @Min(value = 1, message = "난이도는 1 이상이어야 합니다")
    private Integer tierId;

    @Min(value = 1, message = "시간 제한은 1ms 이상이어야 합니다")
    @Max(value = 10000, message = "시간 제한은 10000ms를 초과할 수 없습니다")
    private Integer timeLimit = 2000; // 기본값: 2초

    @Min(value = 1, message = "메모리 제한은 1MB 이상이어야 합니다")
    @Max(value = 1024, message = "메모리 제한은 1024MB를 초과할 수 없습니다")
    private Integer memoryLimit = 256; // 기본값: 256MB
}
```

### Validation 어노테이션 필수 사용
- `@NotNull`: null 불가
- `@NotBlank`: 빈 문자열 불가 (문자열용)
- `@Size`: 길이 제한
- `@Min`, `@Max`: 숫자 범위
- `@Email`: 이메일 형식
- `@Pattern`: 정규식 검증

## 📤 Response DTO 패턴

### 네이밍 규칙
- **단건 응답**: `{Resource}DetailResponse` 또는 `{Resource}Response`
- **목록 응답**: `{Resource}ListResponse` (페이징 적용 시 `PageResponse` 사용)
- **요약 응답**: `{Resource}SummaryResponse`

### Response DTO 작성 규칙

```java
package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 문제 상세 조회 응답 DTO.
 */
@Getter
@Builder
public class ProblemDetailResponse {

    private Long id;
    private String title;
    private String description;
    private Integer tierId;
    private String tierName;
    private Integer timeLimit;
    private Integer memoryLimit;

    // 통계 정보
    private Long totalSubmissions;
    private Long acceptedSubmissions;
    private Double acceptanceRate;

    // 메타 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity에서 Response DTO로 변환.
     */
    public static ProblemDetailResponse from(ProblemEntity problem) {
        return ProblemDetailResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .tierId(problem.getTier().getId())
                .tierName(problem.getTier().getName())
                .timeLimit(problem.getTimeLimit())
                .memoryLimit(problem.getMemoryLimit())
                .totalSubmissions(problem.getStatistics().getTotalSubmissions())
                .acceptedSubmissions(problem.getStatistics().getAcceptedSubmissions())
                .acceptanceRate(problem.getStatistics().getAcceptanceRate())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .build();
    }
}
```

### Response DTO 원칙
- **불변 객체**: `@Builder` + `@Getter`만 사용 (Setter 금지)
- **정적 팩토리 메서드**: `from()`, `of()` 메서드로 Entity → DTO 변환
- **필드명 명확화**: 축약어 지양, 의미 명확한 이름 사용

## 📄 페이징 처리

### 페이징 Request 파라미터

```java
package com.okestro.okestroonlinejudge.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 페이징 요청 파라미터.
 */
@Getter
@Setter
public class PageRequestDto {

    private Integer page = 0;        // 페이지 번호 (0부터 시작)
    private Integer size = 20;       // 페이지 크기 (기본 20)
    private String sort = "id";      // 정렬 기준 필드
    private String direction = "ASC"; // 정렬 방향 (ASC, DESC)

    /**
     * Spring Data JPA Pageable로 변환.
     */
    public Pageable toPageable() {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
```

### 페이징 Response 구조

```java
package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * 페이징 응답 DTO.
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;          // 실제 데이터 목록
    private Integer currentPage;      // 현재 페이지 (0부터 시작)
    private Integer pageSize;         // 페이지 크기
    private Long totalElements;       // 전체 데이터 개수
    private Integer totalPages;       // 전체 페이지 수
    private Boolean first;            // 첫 페이지 여부
    private Boolean last;             // 마지막 페이지 여부
    private Boolean hasNext;          // 다음 페이지 존재 여부
    private Boolean hasPrevious;      // 이전 페이지 존재 여부

    /**
     * Spring Data Page 객체에서 PageResponse로 변환.
     *
     * @param page Spring Data Page 객체
     * @param <T> 데이터 타입
     * @return PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
```

### 페이징 API 예시

**Controller:**
```java
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

    @GetMapping
    public ResponseEntity<PageResponse<ProblemSummaryResponse>> getProblems(
            @ModelAttribute PageRequestDto pageRequest) {

        Page<ProblemEntity> problemPage = problemService.findAll(pageRequest.toPageable());
        Page<ProblemSummaryResponse> responsePage = problemPage.map(ProblemSummaryResponse::from);

        return ResponseEntity.ok(PageResponse.from(responsePage));
    }
}
```

**요청 예시:**
```
GET /api/v1/problems?page=0&size=20&sort=createdAt&direction=DESC
```

**응답 예시:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "두 수의 합",
      "tierId": 5,
      "acceptanceRate": 85.5
    }
  ],
  "currentPage": 0,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

## 🔍 검색 및 필터링

### 검색 파라미터 DTO

```java
/**
 * 문제 검색 요청 DTO.
 */
@Getter
@Setter
public class SearchProblemRequest extends PageRequestDto {

    private String keyword;           // 제목/내용 검색
    private List<Integer> tierIds;    // 난이도 필터
    private List<Long> tagIds;        // 태그 필터
    private String status;            // 문제 상태 (DRAFT, PUBLISHED)

    // 정렬 기본값 오버라이드
    public SearchProblemRequest() {
        super();
        setSort("createdAt");
        setDirection("DESC");
    }
}
```

## ✅ 통일된 에러 응답 형식

### ErrorResponse DTO

```java
package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 DTO.
 */
@Getter
@Builder
public class ErrorResponse {

    private String message;           // 에러 메시지
    private String errorCode;         // 에러 코드 (예: PROBLEM_NOT_FOUND)
    private Integer status;           // HTTP 상태 코드
    private LocalDateTime timestamp;  // 에러 발생 시각
    private List<FieldError> fieldErrors; // 필드 검증 에러 (Optional)

    @Getter
    @Builder
    public static class FieldError {
        private String field;         // 필드명
        private String message;       // 에러 메시지
        private Object rejectedValue; // 거부된 값
    }
}
```

### HTTP 상태 코드 사용 규칙

- `200 OK`: 조회 성공
- `201 Created`: 생성 성공 (Location 헤더에 생성된 리소스 URI 포함)
- `204 No Content`: 삭제 성공 (응답 바디 없음)
- `400 Bad Request`: 요청 파라미터 검증 실패
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `409 Conflict`: 리소스 충돌 (중복 생성 등)
- `500 Internal Server Error`: 서버 오류

## 🔐 API 버저닝

### URL 버저닝 사용
```
/api/v1/problems
/api/v2/problems
```

### 버전 변경 기준
- **Major 버전 변경 (v1 → v2)**: 호환성 깨지는 변경
  - 필수 필드 추가
  - 응답 구조 변경
  - 기존 API 제거

- **Minor 버전 유지**: 하위 호환성 유지
  - 선택 필드 추가
  - 새로운 API 추가

## 📝 API 문서화

### Swagger/OpenAPI 어노테이션 필수

```java
@Tag(name = "Problem", description = "문제 관리 API")
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

    @Operation(summary = "문제 목록 조회", description = "페이징된 문제 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<PageResponse<ProblemSummaryResponse>> getProblems(
            @Parameter(description = "페이징 파라미터") @ModelAttribute PageRequestDto pageRequest) {
        // ...
    }
}
```

## ✅ 체크리스트

API 추가 시 다음 사항을 확인하세요:

- [ ] Request DTO에 Validation 어노테이션 추가
- [ ] Response DTO는 불변 객체 (`@Builder` + `@Getter`)
- [ ] 페이징 필요 시 `PageResponse` 사용
- [ ] 에러 응답은 `ErrorResponse` 형식 준수
- [ ] Swagger 어노테이션 추가
- [ ] HTTP 메서드 및 상태 코드 적절히 사용
- [ ] API 버전 URL에 명시 (`/api/v1/...`)

## 📝 변경 이력

- 2025-12-05: API 설계 원칙 문서 작성
