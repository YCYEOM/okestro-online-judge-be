# 작업 현황 관리 (Work Status)

> 여러 개발자가 동시에 작업할 때 진행 상황을 추적하는 문서입니다.
> 마지막 업데이트: 2025년 12월 10일 (11차)

---

## 📊 프로젝트 개요

| 항목 | 내용 |
|---|---|
| **프로젝트명** | Okestro Online Judge Backend |
| **기술 스택** | Java 21, Spring Boot 3.x, PostgreSQL, JPA |
| **외부 연동** | Judge0, MinIO |
| **현재 버전** | 0.1.1 (개발 중) |
| **Frontend 연동** | okestro-online-judge-fe |

---

## 📋 작업 상태 범례

| 상태 | 의미 | 설명 |
|---|---|---|
| 🟢 TODO | 작업 예정 | 아직 시작하지 않음 |
| 🔵 IN_PROGRESS | 진행 중 | 현재 작업 중 |
| ✅ DONE | 완료 | 작업 완료, FE 연동 가능 |
| 🔴 BLOCKED | 차단됨 | 의존성 또는 이슈로 인해 대기 |
| ⏸️ ON_HOLD | 보류 | 우선순위 낮음 |

---

## 🌿 브랜치 관리 및 작업 할당

### Task ID 시스템

모든 작업에는 **고유한 Task ID**가 부여됩니다. 작업을 시작할 때 이 ID를 사용하여 브랜치를 생성합니다.

**Task ID 형식**: `BE-{Phase}{순번}`
- Phase 1: `BE-1XX` (예: BE-101, BE-102)
- Phase 2: `BE-2XX` (예: BE-201, BE-202)
- Phase 3: `BE-3XX` (예: BE-301, BE-302)
- Phase 4: `BE-4XX` (예: BE-401, BE-402)

### 브랜치 네이밍 규칙

**형식**: `{type}/BE-{task_id}/{assignee}/{short-description}`

**type 종류**:
| Type | 용도 | 예시 |
|---|---|---|
| `feature` | 새 기능 개발 | `feature/BE-106/KDH/judge0-integration` |
| `fix` | 버그 수정 | `fix/BE-201/LJH/submission-error` |
| `refactor` | 리팩토링 | `refactor/BE-101/PSJ/entity-redesign` |
| `docs` | 문서 작업 | `docs/BE-000/KDH/api-docs` |
| `test` | 테스트 추가 | `test/BE-201/KDH/problem-service-test` |

**예시**:
```bash
# Judge0 API 연동 작업 (Task ID: BE-106, 담당자: KDH)
git checkout -b feature/BE-106/KDH/judge0-integration

# 같은 기능의 버그 수정
git checkout -b fix/BE-106/KDH/timeout-handling

# 다른 작업자가 채점 로직 개발 (Task ID: BE-203, 담당자: LJH)
git checkout -b feature/BE-203/LJH/submission-judge
```

### 작업 할당 프로세스

```
1. 📋 Task 목록에서 TODO 상태인 작업 선택
   ↓
2. 📝 WORK_STATUS.md에서 담당자를 본인으로 변경
   ↓
3. 🌿 Task ID로 브랜치 생성
   ↓
4. 💻 개발 진행
   ↓
5. 🔀 PR 생성 (제목에 Task ID 포함)
   ↓
6. ✅ 머지 후 WORK_STATUS.md에서 DONE으로 변경
   ↓
7. 📢 FE팀에 API 사용 가능 알림
```

---

## 📝 Task 목록 (할당 가능)

> **🔔 작업 시작 전**: 아래 목록에서 담당자가 `-`인 Task를 선택하고, 담당자를 본인으로 변경하세요.

### Phase 0: 인증 시스템

| Task ID | 작업 | 상태 | 담당자 | 브랜치 | FE 영향 |
|---------|------|------|--------|--------|---------|
| BE-001 | OAuth2 토큰 발급 (로그인) | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-002 | 토큰 갱신 (Refresh Token) | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-003 | 이메일 인증 코드 발송 | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-004 | 이메일 인증 코드 검증 | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-005 | 회원가입 API | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-006 | 비밀번호 찾기 (임시 비밀번호 발송) | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-007 | 닉네임 중복 확인 | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-008 | 약관 조회 API | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-009 | 현재 사용자 정보 조회 | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | **FE 가능** (2025-12-05) |
| BE-010 | 사용자 정보 수정 | ✅ DONE | AI | feature/BE-010/AI/user-profile-update | **FE 가능** |
| BE-011 | 회원 탈퇴 | ✅ DONE | AI | feature/BE-010/AI/user-profile-update | **FE 가능** |
| BE-012 | Spring Security 설정 | ✅ DONE | KHJ | feature/BE-001/KHJ/auth-system | 없음 |
| BE-013 | TOTP 인증 (이메일 인증 대체) | ✅ DONE | AI | feature/BE-013/AI/totp-auth | **FE 가능** (2025-12-06) |
| BE-014 | TOTP 기반 비밀번호 재설정 | ✅ DONE | AI | feature/BE-014/AI/totp-password-reset | **FE 가능** (2025-12-06) |

### Phase 1: 기본 인프라 구축

| Task ID | 작업 | 상태 | 담당자 | 브랜치 | FE 영향 |
|---------|------|------|--------|--------|---------|
| BE-101 | Spring Boot 프로젝트 셋업 | ✅ DONE | - | - | 없음 |
| BE-102 | JPA Entity 설계 | ✅ DONE | - | - | 없음 |
| BE-103 | Swagger/OpenAPI 설정 | ✅ DONE | - | - | 문서화 |
| BE-104 | MinIO 연동 설정 | ✅ DONE | - | - | 없음 |
| BE-105 | Judge0 Properties 설정 | ✅ DONE | - | - | 없음 |
| BE-106 | Judge0 API 연동 구현 | ✅ DONE | KHJ | feature/BE-106/KHJ/judge0-api-integration | **FE 가능** (2025-12-05) |
| BE-107 | Judge0 Status 매핑 | ✅ DONE | KHJ | feature/BE-107/KHJ/judge0-status-mapping | **FE 가능** (2025-12-05) |

### Phase 2: 핵심 기능 개발

| Task ID | 작업 | 상태 | 담당자 | 브랜치 | FE 영향 |
|---------|------|------|--------|--------|---------|
| BE-201 | 문제 CRUD API | ✅ DONE | - | - | **FE 가능** |
| BE-202 | 테스트케이스 CRUD API | ✅ DONE | - | - | **FE 가능** |
| BE-203 | Submission 채점 로직 | ✅ DONE | KHJ | feature/BE-203/KHJ/submission-judge | **FE 가능** (2025-12-05) |
| BE-204 | 테스트케이스 실행 엔진 | ✅ DONE | Cascade | feature/BE-204-205/Cascade/testcase-engine | **FE 가능** |
| BE-205 | 결과 비교 및 검증 로직 | ✅ DONE | Cascade | feature/BE-204-205/Cascade/testcase-engine | **FE 가능** |
| BE-206 | 제출 히스토리 조회 API | ✅ DONE | KHJ | feature/BE-203/KHJ/submission-judge | **FE 가능** (2025-12-05) |
| BE-207 | 실시간 채점 상태 조회 | ✅ DONE | AI | feature/BE-207/AI/realtime-grading-status | **FE 가능** (2025-12-07) |
| BE-208 | 사용자 통계 집계 | 🟢 TODO | - | - | **FE 차단** |
| BE-209 | 이미지 업로드 API | ✅ DONE | AI | feature/BE-209/AI/image-upload | **FE 가능** (2025-12-06) |
| BE-210 | 이미지 조회 API | ✅ DONE | AI | feature/BE-209/AI/image-upload | **FE 가능** (2025-12-06) |
| BE-211 | 문제 조회수/좋아요/댓글 기능 | ✅ DONE | AI | feature/BE-211/AI/problem-interaction | **FE 가능** (2025-12-09) |
| BE-212 | 테스트케이스 MinIO fallback 수정 | ✅ DONE | AI | feature/BE-212/AI/testcase-minio-fallback | **FE 가능** (2025-12-09) |
| BE-213 | 샘플 테스트케이스 실행 API | 🟢 TODO | - | - | **FE Mock 제거 필요** (ProblemSolve) |
| BE-214 | 추천 문제 목록 API | 🟢 TODO | - | - | **FE Mock 제거 필요** (OJHome) |
| BE-215 | 시도한 문제 목록 API | 🟢 TODO | - | - | **FE Mock 제거 필요** (MyPage) |
| BE-216 | 전체 제출 목록 조회 API | ✅ DONE | AI | main | **FE 가능** (2025-12-09) |
| BE-217 | Solution API 검증 및 문서화 | 🟢 TODO | - | - | **FE Mock 제거 필요** (Solutions) |
| BE-310 | 테스트케이스 동적 CRUD API | ✅ DONE | AI | feature/BE-310/AI/testcase-crud | **FE 가능** (2025-12-06) |
| BE-311 | 문제 검증 API (정답코드 검증) | ✅ DONE | AI | feature/BE-311/AI/problem-validate | **FE 가능** (2025-12-06) |

### Phase 3: 고급 기능

| Task ID | 작업 | 상태 | 담당자 | 브랜치 | FE 영향 |
|---------|------|------|--------|--------|---------|
| BE-301 | 대회 모드 구현 | 🟢 TODO | - | - | 대회 페이지 |
| BE-302 | 실시간 순위표 (랭킹) | 🔵 IN_PROGRESS | AI | feature/BE-302/AI/ranking-system | 순위표 |
| BE-302-1 | 전체 사용자 랭킹 조회 API | ✅ DONE | AI | feature/BE-302/AI/ranking-system | **FE 가능** |
| BE-302-2 | 전체 조직 랭킹 조회 API | ✅ DONE | AI | feature/BE-302/AI/ranking-system | **FE 가능** |
| BE-302-3 | 랭킹 시스템 최적화 | 🔵 IN_PROGRESS | AI | feature/BE-302-3/AI/ranking-optimization | **FE 가능** |
| BE-303 | 코드 플레이그라운드 | 🟢 TODO | - | - | 플레이그라운드 |
| BE-304 | 문제 태그 시스템 | ✅ DONE | AI | feature/BE-304/AI/problem-search-filter | 필터링 |
| BE-305 | 조직 기반 권한 관리 | 🟢 TODO | - | - | 접근 제어 |
| BE-306 | 조직 계층 구조 관리 API | ✅ DONE | AI | feature/BE-306/AI/organization-hierarchy | **FE 가능** (2025-12-10) |
| BE-307 | 사용자 권한 관리 API (ADMIN) | ✅ DONE | AI | feature/BE-307/AI/user-role-management | **FE 가능** (2025-12-10) |
| BE-320 | Solution (정답 공유) API | ✅ DONE | AI | feature/BE-320/AI/solution-architecture | **FE 가능** (2025-12-07) |
| BE-321 | Comment (댓글) API | ✅ DONE | AI | feature/BE-320/AI/solution-architecture | **FE 가능** (2025-12-07) |
| BE-322 | Like (좋아요) 기능 API | ✅ DONE | AI | feature/BE-320/AI/solution-architecture | **FE 가능** (2025-12-07) |
| BE-402-1| 프로필 API 경로 통일 (/oj/profile) | ✅ DONE | AI | feature/BE-010/AI/user-profile-update | **FE 가능** (2025-12-07) |

### Phase 4: 게이미피케이션 시스템

| Task ID | 작업 | 상태 | 담당자 | 브랜치 | FE 영향 |
|---------|------|------|--------|--------|---------|
| BE-401 | 출석 체크 API | ✅ DONE | AI | feature/BE-401/AI/attendance | **FE 가능** (2025-12-06) |
| BE-402 | 사용자 프로필/통계 API | ✅ DONE | AI | feature/BE-402/AI/user-profile | **FE 가능** (2025-12-06) |
| BE-402-1| 프로필 API 경로 통일 (/oj/profile) | 🟢 TODO | - | - | **FE 경로 불일치 해결** |
| BE-403 | 상점 아이템 시스템 | ✅ DONE | AI | feature/BE-403/AI/shop-system | **FE 가능** (2025-12-06) |
| BE-404 | 인벤토리 시스템 | ✅ DONE | AI | feature/BE-403/AI/shop-system | **FE 가능** (2025-12-06) |
| BE-405 | 젬(상점 포인트) 시스템 | ✅ DONE | AI | feature/BE-403/AI/shop-system | **FE 가능** (2025-12-06) |
| BE-406 | 스트릭(잔디) 시스템 | ✅ DONE | AI | feature/BE-406/AI/streak-system | **FE 가능** (2025-12-07) |
| BE-407 | 프로필 공개 설정 API | ✅ DONE | AI | feature/BE-407/AI/privacy-settings | **FE 가능** (2025-12-08) |
| BE-408 | 다른 사용자 프로필 조회 API | ✅ DONE | AI | feature/BE-407/AI/privacy-settings | **FE 가능** (2025-12-08) |
| BE-409 | 문제 생성자 프로필 정보 개선 | ✅ DONE | AI | main | **FE 가능** (2025-12-10) |
| BE-410 | 문제 삭제 기능 (관리자 전용) | ✅ DONE | AI | main | **FE 가능** (2025-12-10) |
| BE-411 | 아바타 전역 업데이트 수정 | ✅ DONE | AI | main | **FE 가능** (2025-12-10) |

---

## 🚧 차단된 작업

| 작업 | 차단 사유 | 해결 방안 | 예상 해결일 |
|---|---|---|---|
| ~~Submission 채점 로직~~ | ~~Judge0 API 연동 미완료~~ | ✅ 완료 | 2025-12-05 |
| 실시간 채점 상태 | SSE/WebSocket 구현 필요 | 채점 로직 완료됨, SSE 구현 예정 | TBD |

---

## 🔥 FE 차단 해소를 위한 우선 작업

> Frontend 개발 진행을 위해 BE에서 우선 완료해야 할 작업

### ✅ 인증 시스템 (완료 - 2025-12-05)

| 우선순위 | 작업 | Task ID | FE 영향 | 상태 |
|----------|------|---------|---------|------|
| **1** | Spring Security 설정 | BE-012 | 모든 인증 기반 | ✅ DONE |
| **2** | OAuth2 토큰 발급 (로그인) | BE-001 | 로그인 페이지 | ✅ DONE |
| **3** | 토큰 갱신 | BE-002 | 세션 유지 | ✅ DONE |
| **4** | 회원가입 API | BE-005 | 회원가입 페이지 | ✅ DONE |
| **5** | 이메일 인증 (발송/검증) | BE-003, 004 | 회원가입 이메일 인증 | ✅ DONE |
| **6** | 비밀번호 찾기 | BE-006 | 비밀번호 찾기 페이지 | ✅ DONE |
| **7** | 문제 목록 검색/필터링 | BE-304 | 문제 목록 페이지 | ✅ DONE |

### 🚨 FE 차단 및 누락 해결 (우선순위 높음)

| 우선순위 | 작업 | Task ID | FE 영향 | 상태 |
|----------|------|---------|---------|------|
| **1** | Solution (정답 공유) API | BE-320 | 정답 코드 보기 페이지 | ✅ DONE |
| **2** | Comment (댓글) API | BE-321 | 정답 코드 상세 (댓글) | ✅ DONE |
| **3** | Like (좋아요) 기능 | BE-322 | 정답 코드 좋아요 | ✅ DONE |
| **4** | 사용자 정보 수정 | BE-010 | 프로필 수정 페이지 | ✅ DONE |
| **5** | 회원 탈퇴 | BE-011 | 계정 관리 페이지 | ✅ DONE |
| **6** | 프로필 API 경로 통일 | BE-402-1 | 마이페이지 | ✅ DONE |

### 채점 기능 (이미 완료)

| 우선순위 | 작업 | 현재 상태 | FE 영향 |
|----------|------|-----------|---------|
| **1** | Judge0 API 연동 완료 | ✅ DONE | 코드 제출 기능 |
| **2** | Submission 채점 로직 | ✅ DONE | 채점 결과 표시 |
| **3** | 제출 결과 조회 API | ✅ DONE | 채점 결과 페이지 |
| **4** | 제출 히스토리 API | ✅ DONE | 제출 현황 페이지 |

### 이미 완료 (FE 작업 가능)

| API | 상태 | FE 활용 |
|-----|------|---------|
| 문제 목록 조회 | ✅ DONE | 문제 목록 페이지 |
| 문제 상세 조회 | ✅ DONE | 문제 상세 페이지 |
| 문제 CRUD | ✅ DONE | 관리자 페이지 |
| 테스트케이스 CRUD | ✅ DONE | 문제 관리 |
| 코드 제출/채점 | ✅ DONE | 코드 제출 기능 |
| 제출 히스토리 조회 | ✅ DONE | 제출 현황 페이지 |

---

## 📊 작업 통계

| 구분 | 완료 | 진행중 | 대기 | 차단 | 합계 |
|---|---|---|---|---|---|
| Phase 0 (인증 시스템) | 10 | 0 | 2 | 0 | 12 |
| Phase 1 (기본 인프라) | 7 | 0 | 0 | 0 | 7 |
| Phase 2 (핵심 기능) | 13 | 0 | 6 | 0 | 19 |
| Phase 3 (고급 기능) | 0 | 0 | 8 | 0 | 8 |
| Phase 4 (게이미피케이션) | 8 | 0 | 1 | 0 | 9 |
| **합계** | **38** | **0** | **17** | **0** | **55** |

**진행률**: 69% 완료 (38/55)

---

## 📝 작업 추가 방법

### 1. 새로운 작업 추가하기

```markdown
| 작업명 | 🟢 TODO | 담당자명 | 우선순위 | FE영향 | 비고 |
```

**우선순위 기준:**
- `CRITICAL` - FE 개발 차단, 즉시 처리 필요
- `HIGH` - 핵심 기능, 1주일 내 완료
- `MEDIUM` - 중요하지만 유연한 일정
- `LOW` - 개선사항, 시간 여유 있을 때

### 2. 작업 상태 업데이트

작업을 시작하면:
```markdown
| 작업명 | 🔵 IN_PROGRESS | 본인이름 | 우선순위 | FE영향 | 시작일: 2025-12-05 |
```

작업을 완료하면:
```markdown
| 작업명 | ✅ DONE | 본인이름 | 우선순위 | **FE 작업 가능** | 완료일: 2025-12-06 |
```

### 3. Git 커밋 메시지 작성

```bash
git commit -m "docs: Update WORK_STATUS.md - [작업명] 상태를 DONE으로 변경"
```

---

## 🔄 작업 흐름 (Workflow)

```
1. WORK_STATUS.md 확인
   ↓
2. FE 차단 해소 작업 우선 선택
   ↓
3. 본인 담당으로 할당, IN_PROGRESS로 변경
   ↓
4. 관련 가이드 문서 확인 (API_DESIGN.md, DATABASE.md 등)
   ↓
5. 코드 작성 및 테스트
   ↓
6. PR 생성 및 리뷰 요청
   ↓
7. WORK_STATUS.md에서 DONE으로 변경
   ↓
8. FE팀에 API 사용 가능 알림
```

---

## 🗓️ 변경 이력

### 2025-12-10 (11차) - 아바타 전역 업데이트 및 문제 생성자 프로필 개선

- **아바타 전역 업데이트 수정 (BE-411)**
  - ShopService.equipItem(): AVATAR 타입 장착 시 UserEntity.profileImage 업데이트 추가
  - ShopService.unequipItem(): AVATAR 타입 해제 시 UserEntity.profileImage null로 설정
  - 영향: 아바타 변경 시 문제 생성자 카드, 댓글 등 모든 곳에서 실시간 반영

- **문제 생성자 프로필 정보 개선 (BE-409)**
  - ProblemService.getProblem(): equipped items 조회 시 AVATAR 케이스 추가
  - 장착된 아바타를 profileImage로 우선 사용, 없으면 UserEntity.profileImage 폴백
  - ProblemDetailResponse.from(): profileImage 파라미터 추가하여 creator 정보에 포함
  - 영향: 문제 상세 페이지에서 생성자의 장착 아이템(아바타, 뱃지, 테두리, 칭호, 배경) 모두 표시

- **문제 삭제 기능 (BE-410)**
  - ProblemController.deleteProblem(): 관리자 권한 문제 삭제 API 추가
  - `@PreAuthorize("hasRole('ADMIN')")` 권한 검증
  - ProblemService.deleteProblem(): cascade 삭제 (testcase, problem_statistics)
  - JwtTokenProvider: JWT 토큰에 role claim 추가
  - 영향: 관리자가 문제 상세 페이지에서 문제 삭제 가능

### 2025-12-09 (10차) - Mock 데이터 정리 및 신규 Task 추가
- **전체 제출 목록 조회 API 구현 완료 (BE-216)**
  - `GET /api/submissions?page={page}&size={size}` 엔드포인트 추가
  - SubmissionController에 getAllSubmissions 메서드 추가
  - SubmissionService에 페이징 및 최신순 정렬 구현
  - SubmissionResponse에 username 필드 추가
  - FE Status.tsx에서 Mock 데이터 제거 및 실제 API 연동 완료

- **프론트엔드 Mock 데이터 정리**
  - FE에서 Mock 데이터 사용 중인 모든 위치 리스트업
  - 신규 Task 추가: BE-213 (샘플 테스트 실행), BE-214 (추천 문제), BE-215 (시도한 문제), BE-217 (Solution API 검증)
  - 각 Mock 데이터별 필요한 API 엔드포인트 및 요청/응답 형식 명시
  - Phase 2 Task 목록에 신규 Task 추가 및 작업 통계 업데이트

### 2025-12-09 (9차) - 문제 조회수/좋아요/댓글 및 제출 오류 수정
- **문제 조회수/좋아요/댓글 기능 구현 완료 (BE-211)**
  - ProblemStatisticsEntity: viewCount, likeCount, commentCount 필드 추가
  - ProblemCommentEntity: 댓글 시스템 (대댓글, 소프트 삭제 지원)
  - ProblemLikeEntity: 좋아요 시스템 (중복 방지)
  - ProblemInteractionService: 조회수 증가, 좋아요 토글, 댓글 CRUD
  - ProblemInteractionController: 상호작용 API 엔드포인트
  - `GET /api/problems/{problemId}/stats` - 통계 조회
  - `POST /api/problems/{problemId}/view` - 조회수 증가
  - `POST /api/problems/{problemId}/like` - 좋아요 토글
  - `GET /api/problems/{problemId}/comments` - 댓글 목록 (페이징)
  - `POST /api/problems/{problemId}/comments` - 댓글 작성 (대댓글 지원)
  - `PUT /api/problems/{problemId}/comments/{commentId}` - 댓글 수정
  - `DELETE /api/problems/{problemId}/comments/{commentId}` - 댓글 삭제 (소프트)

- **테스트케이스 MinIO fallback 수정 (BE-212)**
  - 원인: TestCase 레코드의 inputPath/outputPath가 존재하지 않는 MinIO 파일을 가리켜 제출 시 오류 발생
  - 해결: TestCaseExecutor에 try-catch 추가하여 MinIO 읽기 실패 시 자동으로 DB 컬럼으로 fallback
  - 디버그 로깅 추가로 데이터 출처 추적 가능
  - cleanup_testcase_paths.sql 스크립트 추가 (invalid MinIO path 정리용)
  - 영향: 문제 제출 시 테스트케이스 실행 안정성 향상

- **확인사항**
  - 경험치/젬 보상 시스템은 SubmissionService:134-178에 이미 구현되어 있음
  - 문제 첫 정답 시 자동으로 경험치 및 젬 부여 (중복 방지)

### 2025-12-09 (8차) - 회원가입 버그 수정
- **회원가입 시 activity_public 컬럼 오류 수정**
  - 원인: UserEntity 생성자에서 프라이버시 필드들이 초기화되지 않아 DB INSERT 시 `activity_public doesn't have a default value` 에러 발생
  - 해결: UserEntity에 `initializeDefaults()` 메서드 추가하여 생성자에서 프라이버시 설정 필드들을 명시적으로 초기화
    - `profilePublic = true`
    - `statsPublic = true`
    - `solvedProblemsPublic = true`
    - `activityPublic = true`
    - `createdProblemsCount = 0`
    - `sharedSolutionsCount = 0`
  - 영향 받는 API: `POST /u/auth/sign-up`

### 2025-12-08 (7차) - 프로필 공개 설정 기능 구현
- **프로필 공개 설정 기능 구현 완료 (BE-407, BE-408)**
  - UserEntity에 공개 설정 필드 추가 (profilePublic, statsPublic, solvedProblemsPublic, activityPublic)
  - UserEntity에 커뮤니티 기여도 필드 추가 (createdProblemsCount, sharedSolutionsCount)
  - `GET /oj/users/me/privacy` - 본인 공개 설정 조회
  - `PATCH /oj/users/me/privacy` - 본인 공개 설정 수정
  - `GET /oj/users/{userId}/profile` - 다른 사용자 공개 프로필 조회
  - `GET /oj/users/username/{username}/profile` - 사용자명으로 공개 프로필 조회
  - PublicProfileResponse DTO - 젬(포인트) 제외, 공개 설정에 따른 정보 표시
  - 커뮤니티 기여도(출제한 문제 수, 공유한 솔루션 수)는 항상 공개

### 2025-12-07 (6차) - 스트릭(잔디) 시스템 구현
- **스트릭 기능 구현 완료 (BE-406)**
  - `GET /oj/users/me/streak` API 추가
  - SubmissionRepository: 날짜별 해결 문제 수 집계 쿼리 구현
  - UserService: 연도별 스트릭 조회, 최장/현재 스트릭 계산 로직
  - Frontend: GitHub 스타일 잔디 그래프 컴포넌트(`StreakGraph`) 구현 및 연동

### 2025-12-07 (5차) - FE 차단 해소 및 누락 기능 Task 추가
- **FE-BE 간 기능 누락 식별 및 Task 추가**
  - `BE-320`: Solution (정답 공유) API 추가
  - `BE-321`: Comment (댓글) API 추가
  - `BE-322`: Like (좋아요) 기능 추가
- **API 경로 불일치 해결 Task 추가**
  - `BE-402-1`: 프로필 API 경로 통일 (`/oj/users` vs `/oj/profile`)
- **우선순위 조정**
  - 사용자 정보 수정(`BE-010`) 및 탈퇴(`BE-011`) 우선순위 상향
  - 정답 공유 및 댓글 시스템을 FE 차단 해소 우선 작업으로 설정

### 2025-12-06 (4차) - 상점 시스템 완성
- **상점 시스템 통합 완료**
  - FE와 BE 상점/인벤토리 시스템 연동 확인
  - 아이템 구매, 장착/해제 기능 검증
  - DiceBear 아바타 URL 생성 로직 통일
  - 사용자 프로필에 장착 아이템 반영

- **버그 수정 및 개선**
  - React Hooks 순서 문제 수정 (ProblemSolve)
  - 네비게이션 메뉴 최적화
  - 다국어 지원 (한글화)

### 2025-12-06 (3차) - 상점 시스템 구현
- **상점/인벤토리 시스템 구현 완료 (BE-403, BE-404, BE-405)**
  - ShopItemEntity - 상점 아이템 (아바타, 테두리, 뱃지, 칭호, 페인트)
  - ShopItemType 열거형 (AVATAR, PROFILE_BORDER, BADGE, TITLE, NAME_COLOR)
  - UserInventoryEntity - 사용자 인벤토리 (구매 아이템 관리)
  - GemTransactionEntity - 젬 거래 이력
  - ShopService, ShopController - 상점 API
  - 아이템 구매, 장착/해제, 인벤토리 조회 기능

- **FE 상점/인벤토리 페이지 구현**
  - ShopPage.tsx - 상점 페이지 (DiceBear 아바타 미리보기 포함)
  - InventoryPage.tsx - 인벤토리 페이지 (장착 관리)
  - shop.d.ts - 상점 타입 정의
  - shop-api.ts - 상점 API 함수
  - 라우터 및 네비게이션 추가

### 2025-12-06 (2차)
- **TestCaseEntity DB 스키마 수정**
  - `expectedOutput` → `output` 컬럼명 매핑 수정 (DB 스키마에 맞춤)
  - `input`, `output` 필드 추가 (직접 데이터 저장)
  - `inputPath`, `outputPath` nullable로 변경 (MinIO 경로, 선택적)
  - TestCaseService에서 input/output 값 설정 추가

- **Judge0Client 디버그 로깅 추가**
  - 채점 결과 상세 로깅 (statusId, stderr, compileOutput, message, stdout)
  - Go 컴파일 시간 초과 원인 분석 지원

- **Judge0 설정 조정**
  - `max-file-size: 4096` 추가 (Judge0 서버 제한에 맞춤)
  - `cpu-time-limit: 5.0`으로 증가
  - `max-retries: 30`, `retry-delay-ms: 2000`으로 조정

### 2025-12-06 (1차)
- **이미지 업로드/조회 API 구현 완료 (BE-209, BE-210)**
  - POST /api/images - 이미지 업로드 (MinIO 저장)
  - GET /api/images/{fileName} - 이미지 조회 (스트리밍)
  - ImageController, ImageService 신규 생성
  - SecurityConfig에 /api/images/** permitAll 추가

- **테스트케이스 동적 CRUD API 구현 완료 (BE-310)**
  - POST /api/problems/{problemId}/testcases - 테스트케이스 생성
  - GET /api/problems/{problemId}/testcases - 테스트케이스 목록 조회
  - DELETE /api/testcases/{id} - 테스트케이스 삭제
  - TestCaseController, TestCaseService 구현

- **문제 검증 API 구현 완료 (BE-311)**
  - POST /api/problems/validate - 정답코드로 테스트케이스 검증
  - ValidationService 신규 생성
  - ValidateProblemRequest, ValidationResponse DTO 생성
  - Judge0Client를 사용하여 실제 코드 실행 및 검증

### 2025-12-05
- WORK_STATUS.md 전면 개편
- BE-FE API 연동 현황 테이블 추가
- Phase별 작업 현황 구조화
- FE 차단 해소 우선순위 목록 추가
- FE 영향 컬럼 추가
- **인증 시스템 구현 완료 (BE-001~009, BE-012)**
  - JWT 기반 로그인/토큰 갱신
  - 회원가입, 닉네임/이메일 중복 확인
  - 이메일 인증 코드 발송/검증 (개발 모드)
  - 비밀번호 찾기 (개발 모드)
  - 현재 사용자 정보 조회
  - Spring Security + JWT 필터 설정

---

## 🐞 개선리포트 반영 - 버그/개선 항목

> 파일: 개선리포트.doc (2025-12-07)에서 발췌한 주요 이슈를 작업 항목으로 등록합니다.

| 항목 | 문제 | 개선방향(초안) | 상태 | 담당자 | 우선순위 | 비고 |
|---|---|---|---|---|---|---|
| 비밀번호 찾기 경로 오류 | /auth/undefined/auth/password-recovery로 이동됨(잘못된 요청으로 빠짐) | 라우팅 경로 생성 로직 수정, basePath 중복 제거. E2E 라우팅 테스트 추가 | 🟢 TODO | - | HIGH | FE 주도, BE 영향 없음 |
| 프로필 수정 UI 스크롤 점프 | 소속 수정 시 화면이 위로 이동 | Form 상태 유지 및 Scroll Restoration 적용, 레이아웃 리플로우 점검 | 🟢 TODO | - | MEDIUM | FE |
| 회원가입 QR 코드 미생성 | 회원가입 시 QR 코드 생성 실패 | QR 라이브러리/의존성 점검, 생성 트리거 확인. 필요 시 BE 발급 API 점검 | 🟢 TODO | - | HIGH | FE 주, BE 영향 가능 |

> 담당자 할당 및 브랜치 생성 시, 브랜치 네이밍은 `fix/BE-{task_id}/{assignee}/{short-description}` 규칙을 따릅니다.

---

## 😕 개선리포트 반영 - 불편한점

> 파일: 개선리포트.doc (2025-12-07) 중 사용성 관련 불편 사항을 작업 항목으로 등록합니다.

| 항목 | 문제 | 개선방향(초안) | 상태 | 담당자 | 우선순위 | 비고 |
|---|---|---|---|---|---|---|
| 로그인 페이지 | 메인페이지로 이동할 수 없음 | 상단 로고/홈 링크 동작 보장, 헤더 네비게이션 노출, 라우터 fallback 설정, E2E 내비게이션 테스트 추가 | 🟢 TODO | - | MEDIUM | FE |
| 다크 모드 | 검은 글자색으로 요소가 보이지 않음 (예: 문제 풀러가기) | 다크 테마 색상 토큰/콘트라스트 점검(최소 4.5:1), 컴포넌트 변형 상태 색상 검토 | 🟢 TODO | - | HIGH | FE |
| 화이트 모드 | 밝은 글자색 가시성 저하 (예: 랭킹의 등급) | 라이트 테마 팔레트/콘트라스트 개선, 의미 색상 재정의 및 접근성 점검 | 🟢 TODO | - | MEDIUM | FE |

---

## 🧪 프론트엔드 Mock 데이터 정리 (API 연동 대기 목록)

> 프론트엔드에서 Mock 데이터를 사용하고 있는 기능 목록입니다. BE API 구현이 완료되면 실제 데이터로 교체해야 합니다.

### ✅ 이미 실제 API로 교체된 기능
| 페이지/컴포넌트 | 기능 | 상태 | 비고 |
|---------------|------|------|------|
| Status.tsx | 채점 현황 목록 | ✅ 완료 | getAllSubmissions API 연동 완료 (2025-12-09) |
| ProblemList.tsx | 문제 목록 | ✅ 완료 | getProblems API 연동 완료 |
| ProblemDetail.tsx | 문제 상세 | ✅ 완료 | getProblem API 연동 완료 |

### 🔴 Mock 데이터 사용 중 (BE API 필요)

#### 1. 코드 실행 (샘플 테스트)
**파일**: `src/pages/oj/ProblemSolve.tsx` (Line 286-309)
```typescript
// Mock 샘플 실행 결과
const mockResults: TestCaseResult[] = [
    {
        testCaseId: 1,
        status: 'ACCEPTED',
        runtime: 24,
        memory: 9856,
        input: '1 2',
        expectedOutput: '3',
        actualOutput: '3',
        isHidden: false,
    },
    // ...
];
```
**필요 API**: 
- `POST /api/problems/{id}/run` - 샘플 테스트케이스 실행
- Request: `{ sourceCode, language, testCaseIds? }`
- Response: `{ testCaseResults: TestCaseResult[] }`

**Task ID**: `BE-213` (신규)
**우선순위**: HIGH
**FE 영향**: 코드 실행 버튼 기능

---

#### 2. 홈페이지 추천 문제 목록
**파일**: `src/pages/oj/OJHome.tsx` (Line 90-94)
```typescript
// Mock featured problems
const featuredProblems = [
    { id: 1000, title: '두 수의 합', difficulty: 'EASY', acceptance: '65%', solvers: 12500 },
    { id: 1001, title: '행렬 곱셈 최적화', difficulty: 'MEDIUM', acceptance: '42%', solvers: 5400 },
    { id: 1002, title: '최단 경로 찾기', difficulty: 'HARD', acceptance: '28%', solvers: 1200 },
];
```
**필요 API**: 
- `GET /api/problems/featured?limit=3` - 추천 문제 목록
- Response: `{ problems: Problem[], totalCount: number }`

**Task ID**: `BE-214` (신규)
**우선순위**: MEDIUM
**FE 영향**: 홈페이지 추천 문제 섹션

---

#### 3. 홈페이지 상위 랭커 목록
**파일**: `src/pages/oj/OJHome.tsx` (Line 97-101)
```typescript
// Top rankers mock
const topRankers = [
    { rank: 1, username: 'CodeMaster', tier: { group: 'RUBY', level: 1 }, solved: 1247 },
    { rank: 2, username: 'AlgoKing', tier: { group: 'DIAMOND', level: 2 }, solved: 1189 },
    { rank: 3, username: 'ByteWizard', tier: { group: 'DIAMOND', level: 5 }, solved: 1056 },
];
```
**필요 API**: 
- `GET /api/rankings/top?limit=3` - 상위 랭커 (이미 구현됨, 확인 필요)
- Response: `{ rankings: UserRanking[] }`

**Task ID**: `BE-302-1` (이미 완료?)
**우선순위**: LOW
**FE 영향**: 홈페이지 랭킹 미리보기

---

#### 4. 정답 코드 목록 (Solutions)
**파일**: `src/pages/oj/Solutions.tsx` (Line 19-44)
```typescript
// Mock: 정답 여부 확인 (실제로는 API 호출)
const solvedProblems = [1000, 1003, 1005, 1009];
setIsSolved(solvedProblems.includes(Number(problemId)));

// Mock Data - 정답 코드 목록
const solutions: Solution[] = [
    {
        id: 'sol-1',
        problemId: Number(problemId),
        problemTitle: problemTitle,
        userId: 'user-1',
        userName: 'CodeMaster',
        userProfileImage: '',
        language: 'Python',
        code: 'a, b = map(int, input().split())\nprint(a + b)',
        runtime: '28ms',
        memory: '9.1MB',
        submittedAt: '2025-12-05T10:30:00',
        likeCount: 42,
        commentCount: 5,
        isLiked: false,
    },
    // ...
];
```
**필요 API**: 
- `GET /api/problems/{problemId}/solutions` - 정답 코드 목록 (BE-320 완료?)
- `GET /api/problems/{problemId}/solved` - 사용자가 해당 문제를 풀었는지 확인
- Response: `{ solutions: Solution[], isSolved: boolean }`

**Task ID**: `BE-320` (이미 완료?)
**우선순위**: HIGH
**FE 영향**: 정답 코드 보기 페이지

---

#### 5. 정답 코드 상세 및 댓글
**파일**: `src/pages/oj/SolutionDetail.tsx` (Line 21-86)
```typescript
// Mock Data
const mockSolution: Solution = {
    id: solutionId!,
    problemId: 1000,
    problemTitle: '두 수의 합',
    userId: 'user-1',
    userName: 'CodeMaster',
    userProfileImage: '',
    language: 'Python',
    code: 'a, b = map(int, input().split())\nprint(a + b)',
    runtime: '28ms',
    memory: '9.1MB',
    submittedAt: '2025-12-05T10:30:00',
    likeCount: 42,
    commentCount: 5,
    isLiked: false,
};

const mockComments: Comment[] = [
    {
        id: 'comment-1',
        userId: 'user-2',
        userName: 'AlgoFan',
        userProfileImage: '',
        content: '깔끔한 코드네요! 참고하겠습니다.',
        createdAt: '2025-12-05T11:00:00',
        likeCount: 3,
        isLiked: false,
    },
    // ...
];
```
**필요 API**: 
- `GET /api/solutions/{solutionId}` - 정답 코드 상세 (BE-320 완료?)
- `GET /api/solutions/{solutionId}/comments` - 댓글 목록 (BE-321 완료?)
- `POST /api/solutions/{solutionId}/like` - 좋아요 토글 (BE-322 완료?)

**Task ID**: `BE-320, BE-321, BE-322` (이미 완료?)
**우선순위**: HIGH
**FE 영향**: 정답 코드 상세 페이지

---

#### 6. 마이페이지 - 시도한 문제 목록
**파일**: `src/pages/oj/MyPage.tsx` (Line 149-153)
```typescript
// Mock attempted problems for now (API 추후 연동)
const mockAttemptedProblems: AttemptedProblem[] = [
    // 빈 배열
];
setAttemptedProblems(mockAttemptedProblems);
```
**필요 API**: 
- `GET /api/users/me/attempted-problems` - 시도한 문제 목록
- Response: `{ problems: AttemptedProblem[] }`

**Task ID**: `BE-215` (신규)
**우선순위**: MEDIUM
**FE 영향**: 마이페이지 문제 히스토리

---

#### 7. 상점 페이지 (개발용 주석)
**파일**: `src/pages/oj/ShopPage.tsx` (Line 91)
```typescript
// Mock data for development
```
**상태**: 실제 API 사용 중 (주석만 남아있음)
**Task ID**: 해당 없음

---

#### 8. 인벤토리 페이지 (개발용 주석)
**파일**: `src/pages/oj/InventoryPage.tsx` (Line 93)
```typescript
// Mock data for development
```
**상태**: 실제 API 사용 중 (주석만 남아있음)
**Task ID**: 해당 없음

---

#### 9. OJLayout - 장착 아이템 Mock Fallback
**파일**: `src/ui/layout/OJLayout.tsx` (Line 247-380)
```typescript
// 개발용 Mock 데이터 (API 실패 시 사용)
const MOCK_EQUIPPED_ITEMS: InventoryItem[] = [
    {
        id: '999',
        shopItemId: 'default-avatar',
        name: '기본 아바타',
        type: 'AVATAR',
        rarity: 'COMMON',
        imageUrl: '/images/avatars/default.png',
        isEquipped: true,
        purchasedAt: new Date().toISOString(),
    },
    // ...
];

const MOCK_USER_STATS = {
    tier: { group: 'BRONZE', level: 5 },
    gems: 0,
};
```
**상태**: API 연동되어 있으나 fallback으로 Mock 사용
**Task ID**: 해당 없음 (정상 동작)

---

### 🆕 신규 Task 추가 필요

| Task ID | 작업 | 우선순위 | FE 영향 | 상태 |
|---------|------|---------|---------|------|
| BE-213 | 샘플 테스트케이스 실행 API | HIGH | 코드 실행 버튼 | 🟢 TODO |
| BE-214 | 추천 문제 목록 API | MEDIUM | 홈페이지 추천 문제 | 🟢 TODO |
| BE-215 | 시도한 문제 목록 API | MEDIUM | 마이페이지 히스토리 | 🟢 TODO |
| BE-320 | Solution API 검증 및 테스트 | HIGH | 정답 코드 페이지 | 🔵 확인 필요 |
| BE-321 | Comment API 검증 및 테스트 | HIGH | 댓글 기능 | 🔵 확인 필요 |
| BE-322 | Like API 검증 및 테스트 | MEDIUM | 좋아요 기능 | 🔵 확인 필요 |

---

## 📌 참고 문서

- [FE 작업 현황](../../../okestro-online-judge-fe/docs/WORK_STATUS.md)
- [API 설계 가이드](./API_DESIGN.md)
- [데이터베이스 가이드](./DATABASE.md)
- [아키텍처 가이드](./ARCHITECTURE.md)
- [Judge0 가이드](./JUDGE0.md)

---

## ✅ 최근 완료된 작업 (2025-12-10, 11차)

### 닉네임 시스템 구현 (BE-420)

**기능 구현:**
- **UserEntity 개선**:
  - `username`: 사용자명(이름), 변경 불가, 중복 가능 (unique 제약 제거)
  - `nickname`: 닉네임, 변경 가능, 고유값 (unique 제약 유지)
  - `updateNickname(String nickname)` 메서드 추가

- **ShopItemType 확장**:
  - `NICKNAME_CHANGE` enum 추가 (닉네임 변경권)

- **DTO 업데이트**:
  - `UserResponse`: nickname 필드 추가
  - `UserProfileResponse`: nickname 필드 추가
  - `PublicProfileResponse`: nickname 필드 추가
  - `SignUpRequest`: nickname 필드 추가 (@Size(min=2, max=19))

- **Repository 확장**:
  - `UserRepository.existsByNickname(String nickname)` - 중복 검사
  - `UserRepository.findByNickname(String nickname)` - 닉네임 조회
  - `UserInventoryRepository.findByUserIdAndItemType()` - 아이템 검증

- **Service 로직**:
  - `AuthService.signUp()`: 닉네임 중복 검사 추가
  - `AuthService.isNicknameAvailable()`: 닉네임 사용 가능 확인
  - `UserService.updateProfile()`: username 제외 (변경 불가)
  - `UserService.updateNickname()`: 
    - 닉네임 중복 검사
    - NICKNAME_CHANGE 아이템 검증
    - 아이템 소비 (삭제)
    - 닉네임 업데이트

- **Controller**:
  - `AuthController.checkNickname()`: GET /u/auth/check-nickname 업데이트
  - `UserController.updateMyNickname()`: PUT /oj/users/me/nickname 신규
    - 요청 body: `{"nickname": "newNickname"}`
    - 검증: 2-19자, 중복 확인, 변경권 소유
    - HTTP 400 에러 반환 수정 (기존 200 → 400)

**Database**:
- `data-shop.sql`: NICKNAME_CHANGE 아이템 추가 (300 gems, RARE)
- `add_nickname_column.sql`: 마이그레이션 스크립트 (4단계)

**버그 수정:**
- HTTP Status Code 수정: 에러 발생 시 200 → 400 반환
- DTO에 누락된 nickname 필드 추가

**변경된 파일:**
- `src/main/java/com/okestro/okestroonlinejudge/domain/UserEntity.java`
- `src/main/java/com/okestro/okestroonlinejudge/domain/ShopItemType.java`
- `src/main/java/com/okestro/okestroonlinejudge/dto/request/SignUpRequest.java`
- `src/main/java/com/okestro/okestroonlinejudge/dto/response/UserResponse.java`
- `src/main/java/com/okestro/okestroonlinejudge/dto/response/UserProfileResponse.java`
- `src/main/java/com/okestro/okestroonlinejudge/dto/response/PublicProfileResponse.java`
- `src/main/java/com/okestro/okestroonlinejudge/repository/UserRepository.java`
- `src/main/java/com/okestro/okestroonlinejudge/service/AuthService.java`
- `src/main/java/com/okestro/okestroonlinejudge/service/UserService.java`
- `src/main/java/com/okestro/okestroonlinejudge/controller/AuthController.java`
- `src/main/java/com/okestro/okestroonlinejudge/controller/UserController.java`
- `src/main/resources/data-shop.sql`
- `src/main/resources/db/migration/add_nickname_column.sql`

**FE 영향**: 닉네임 시스템 완전 지원 - 회원가입, 프로필, 닉네임 변경 기능 사용 가능

