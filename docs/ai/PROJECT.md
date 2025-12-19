# 프로젝트 개요

## 🎯 프로젝트 목표

**Okestro Online Judge**는 Judge0를 기반으로 한 커스텀 온라인 저지 시스템입니다.

### 핵심 목표
1. **Judge0 기반 커스터마이징**: Judge0 API를 활용하되, 자체 채점 로직 및 확장 기능 구현
2. **고성능 채점 시스템**: 대량의 제출 요청을 효율적으로 처리
3. **다양한 언어 지원**: 13개 프로그래밍 언어 지원 (Java, Python, C++, JavaScript 등)
4. **확장 가능한 아키텍처**: 향후 대회 모드, 실시간 순위표 등 기능 추가 용이

## 📌 프로젝트 범위

### Phase 1: 기본 인프라 (현재)
- ✅ Spring Boot 기반 백엔드 구축
- ✅ ERD 기반 엔티티 설계
- ✅ Swagger API 문서화
- 🔵 Judge0 API 연동
- 🟢 MinIO 파일 스토리지 연동

### Phase 2: 핵심 기능
- 문제 관리 (CRUD, 테스트케이스 관리)
- 제출 및 채점 시스템
- 실시간 채점 상태 조회
- 사용자 통계 및 문제 통계

### Phase 3: 고급 기능
- 대회 모드 (제한 시간, 순위표)
- 코드 플레이그라운드
- 문제 태그 및 난이도 시스템
- 조직(Organization) 기반 권한 관리

## 🛠 기술 스택

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (or MySQL)
- **ORM**: Spring Data JPA
- **Documentation**: Swagger/OpenAPI 3.0
- **Storage**: MinIO (S3 compatible)

### Judge Engine
- **Judge0**: Code execution engine
- **Supported Languages**: 13개 (Bash, C, C++, C#, Go, Java, JavaScript, Kotlin, Python3, Ruby, Rust, Swift, TypeScript)

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **Version Control**: Git

## 🎨 Judge0 커스터마이징 전략

### Judge0 사용 범위
- **코드 실행 엔진**: Judge0의 샌드박스 환경 활용
- **언어별 컴파일러**: Judge0가 제공하는 13개 언어 컴파일러 사용
- **리소스 제한**: 메모리, CPU 시간 제한 설정

### 자체 구현 영역
- **테스트케이스 관리**: 자체 DB에서 관리
- **결과 비교 로직**: 커스텀 정확도 검증 (공백, 줄바꿈 처리)
- **부분 점수**: 테스트케이스별 점수 배분
- **통계 집계**: 문제별/사용자별 통계 자체 계산
- **권한 관리**: 문제 공개/비공개, 조직별 접근 제어

## 📊 핵심 도메인

### 1. Problem (문제)
- 문제 설명, 제약사항, 예제
- 난이도(Tier), 태그
- 테스트케이스 관리

### 2. Submission (제출)
- 사용자 코드 제출
- Judge0 연동 채점
- 채점 결과 저장

### 3. User (사용자)
- 인증 및 권한 관리
- 제출 히스토리
- 통계 (해결한 문제 수, 정확도 등)

### 4. Organization (조직)
- 기업/학교별 문제 그룹
- 조직 내 사용자 관리

## 🚀 성공 기준

### 기능적 요구사항
- [ ] 문제 등록 및 수정 가능
- [ ] 13개 언어로 코드 제출 및 채점 가능
- [ ] 실시간 채점 결과 조회
- [ ] 사용자별 제출 히스토리 조회
- [ ] 문제별 정답률 통계 제공

### 비기능적 요구사항
- [ ] 동시 100개 이상 제출 처리 가능
- [ ] 채점 결과 5초 이내 반환
- [ ] API 응답 시간 500ms 이하
- [ ] 99% 이상 가용성

## 📖 참고 자료

- [Judge0 공식 문서](https://ce.judge0.com/)
- [Judge0 GitHub](https://github.com/judge0/judge0)
- Spring Boot 3.x 문서
- Java 21 Language Features

## 📝 변경 이력

- 2025-12-05: 프로젝트 초기 설계 및 문서 작성
