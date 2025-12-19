# Okestro Online Judge - AI 협업 가이드라인

이 디렉토리는 AI와 함께 작업할 때 참고할 프로젝트 가이드라인과 규칙을 포함합니다.

## 📚 문서 목록

### 0. [작업 현황 관리](WORK_STATUS.md) ⭐
- 현재 진행 중인 작업 목록
- 담당자 및 우선순위
- 작업 상태 추적
- 작업 추가 및 업데이트 방법

### 1. [프로젝트 개요](PROJECT.md)
- 프로젝트 목표 및 범위
- Judge0 기반 커스텀 저지 서버 개발 방향
- 핵심 기능 및 요구사항

### 2. [아키텍처 가이드](ARCHITECTURE.md)
- 레이어 아키텍처 구조
- 패키지 구조 및 모듈 분리
- 의존성 관리 원칙

### 3. [코딩 컨벤션](CONVENTIONS.md)
- Java 21 코딩 스타일
- 네이밍 규칙
- 포매팅 및 주석 작성법
- 최신 Java 기능 활용법

### 4. [Judge0 커스터마이징](JUDGE0.md)
- Judge0 API 통합 방법
- 커스텀 저지 로직 구현
- 언어별 설정 및 제한사항
- 채점 결과 처리 전략

### 5. [API 설계 원칙](API_DESIGN.md)
- REST API 설계 규칙
- Request/Response DTO 패턴
- 페이징 및 정렬 처리
- 에러 응답 형식
- API 버저닝 전략

### 6. [데이터베이스 가이드](DATABASE.md)
- ERD 기반 엔티티 설계
- JPA 사용 규칙
- 연관관계 매핑 전략
- 쿼리 최적화 가이드

### 7. [AI 작업 규칙](AI_RULES.md)
- AI에게 작업 요청 시 필수 규칙
- 코드 리뷰 체크리스트
- 금지 사항 및 주의사항
- 협업 워크플로우

## 🎯 빠른 참조

### 작업 시작 전 (필수!)
1. [WORK_STATUS.md](WORK_STATUS.md) - 작업 선택 및 상태 업데이트
2. [AI_RULES.md](AI_RULES.md) - AI 작업 규칙 확인

### 새로운 API 추가 시
1. [API_DESIGN.md](API_DESIGN.md) - Request/Response DTO 패턴 확인
2. [CONVENTIONS.md](CONVENTIONS.md) - 네이밍 규칙 확인
3. [ARCHITECTURE.md](ARCHITECTURE.md) - 레이어별 책임 확인

### Judge0 관련 작업 시
1. [JUDGE0.md](JUDGE0.md) - Judge0 API 연동 방법 확인
2. [DATABASE.md](DATABASE.md) - Submission 엔티티 구조 확인

### 새로운 엔티티 추가 시
1. [DATABASE.md](DATABASE.md) - 엔티티 설계 규칙 확인
2. [CONVENTIONS.md](CONVENTIONS.md) - 네이밍 및 어노테이션 규칙 확인

---

## 🔐 권한 기반 접근 제어 (Method Security)

Spring Security의 `@EnableMethodSecurity`가 활성화되어 있어 **메서드 레벨에서 권한 검사**가 가능합니다.

### 사용 가능한 어노테이션

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@PreAuthorize("hasRole('ADMIN')")` | ADMIN 권한만 접근 가능 | 관리자 전용 API |
| `@PreAuthorize("hasRole('USER')")` | USER 이상 권한 접근 가능 | 일반 사용자 API |
| `@PreAuthorize("isAuthenticated()")` | 로그인한 사용자만 접근 가능 | 인증 필요 API |
| `@PreAuthorize("permitAll()")` | 모든 사용자 접근 가능 | 공개 API |

### 사용 예시

```java
// 클래스 레벨 - 컨트롤러 전체에 적용
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // 모든 메서드가 ADMIN 권한 필요
}

// 메서드 레벨 - 특정 메서드에만 적용
@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @GetMapping
    public List<Problem> getProblems() {
        // 인증만 되면 접근 가능 (기본)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Problem createProblem() {
        // ADMIN만 문제 생성 가능
    }
}
```

### 현재 권한 구조

| Role | 설명 | 접근 가능 API |
|------|------|--------------|
| `USER` | 일반 사용자 | 문제 풀이, 제출, 프로필 등 |
| `ADMIN` | 관리자 | USER 권한 + 사용자 관리, 문제 관리 등 |

### 권한 검사 실패 시

- **403 Forbidden** 응답 반환
- 로그인은 되어 있지만 권한이 부족한 경우

### 관리자 전용 API 목록

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/admin/users` | 전체 사용자 목록 조회 |
| GET | `/api/admin/users/{userId}` | 특정 사용자 조회 |
| PATCH | `/api/admin/users/{userId}/role` | 사용자 권한 변경 |

---

## 🔄 문서 업데이트 원칙

- 새로운 패턴이나 규칙이 정립되면 즉시 문서 업데이트
- 모든 팀원이 동의한 내용만 문서에 반영
- 변경 이력은 Git 커밋 메시지로 관리
- 문서는 항상 최신 코드 상태를 반영해야 함

## 📝 작성 일자

- 최초 작성: 2025-12-05
- 마지막 업데이트: 2025-12-10
