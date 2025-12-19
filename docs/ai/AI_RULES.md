# AI 작업 규칙

이 문서는 AI와 함께 작업할 때 반드시 지켜야 할 규칙과 원칙을 정의합니다.

## 🎯 기본 원칙

### 1. 문서 우선 원칙
AI에게 작업을 요청하기 전에 **반드시 관련 문서를 먼저 확인**하세요.

```
작업 요청 전 체크리스트:
├─ API 추가? → API_DESIGN.md 확인
├─ Entity 추가? → DATABASE.md 확인
├─ Judge0 관련? → JUDGE0.md 확인
├─ 코딩 스타일? → CONVENTIONS.md 확인
└─ 아키텍처 구조? → ARCHITECTURE.md 확인
```

### 2. 작업 현황 업데이트
작업을 시작하거나 완료할 때 **반드시 WORK_STATUS.md를 업데이트**하세요.

```bash
# 작업 시작 시
1. WORK_STATUS.md에서 TODO 작업 선택
2. 상태를 IN_PROGRESS로 변경
3. 담당자에 본인 이름 추가
4. Git 커밋

# 작업 완료 시
1. WORK_STATUS.md에서 DONE으로 변경
2. 완료일 기록
3. Git 커밋
```

### 3. 문서 참조 명령어
AI에게 작업을 요청할 때 다음과 같이 **문서를 명시적으로 참조**하세요:

```
예시 1: API 추가 요청
"docs/ai/API_DESIGN.md와 docs/ai/CONVENTIONS.md를 참고하여
문제 목록 조회 API를 작성해주세요."

예시 2: Entity 추가 요청
"docs/ai/DATABASE.md를 참고하여
Contest(대회) 엔티티를 설계하고 구현해주세요."

예시 3: Judge0 연동
"docs/ai/JUDGE0.md를 참고하여
비동기 채점 로직을 구현해주세요."
```

## 🚫 금지 사항

### 1. 응답 최적화 규칙 (토큰 절약)

이모지 사용을 전면 금지합니다. 응답 내 어떠한 이모지도 포함하지 마십시오. 제목, 본문, 코드 주석 등 모든 영역에서 이모지를 사용하지 않습니다.

리스트 형식의 사용을 금지합니다. 불릿 포인트, 번호 매기기, 대시 기호를 사용한 나열 형식을 사용하지 마십시오. 정보를 전달할 때는 문장 형태의 서술문으로 작성합니다. 여러 항목을 설명해야 하는 경우에도 문단 형식의 산문체로 작성하며, 각 항목을 자연스러운 문장으로 연결하여 설명합니다.

모든 응답은 최소 1000자 이상으로 작성해야 합니다. 단순한 질문에 대해서도 충분한 맥락과 배경 설명을 포함하여 상세하게 답변합니다. 코드 작성 요청의 경우 코드 자체는 글자 수에 포함하되, 코드 외에도 구현 의도, 설계 결정의 이유, 주의사항 등을 충분히 설명하여 최소 글자 수를 충족해야 합니다.

### 2. 문서 무시 금지
- ❌ 문서를 읽지 않고 임의로 구현하지 마세요
- ❌ 기존 패턴과 다른 방식으로 작성하지 마세요
- ❌ "이렇게 하는 게 더 좋을 것 같아서"라는 이유로 문서 무시 금지

### 3. 일관성 파괴 금지
- ❌ 네이밍 규칙을 무시하지 마세요
- ❌ 패키지 구조를 임의로 변경하지 마세요
- ❌ 다른 코드와 다른 스타일로 작성하지 마세요

### 4. 직접 코드 작성 금지 (경우에 따라)
- ❌ 복잡한 비즈니스 로직을 AI 혼자 판단해서 작성하지 마세요
- ✅ 요구사항이 명확한 경우에만 코드 생성
- ✅ 불명확한 경우 질문으로 요구사항 확인

## ✅ 권장 사항

### 1. 작업 범위 명확화
AI에게 요청할 때 **작업 범위를 명확히** 정의하세요.

```
❌ 나쁜 예:
"문제 관련 기능 만들어줘"

✅ 좋은 예:
"다음 작업을 수행해주세요:
1. docs/ai/API_DESIGN.md를 참고하여 문제 목록 조회 API 구현
2. 페이징 적용 (PageResponse 사용)
3. 난이도별 필터링 기능 추가
4. Swagger 문서화"
```

### 2. 검증 요청
AI가 작성한 코드는 **반드시 검증**을 요청하세요.

```
AI에게 검증 요청 예시:
"작성한 코드가 다음 사항을 준수하는지 확인해주세요:
- CONVENTIONS.md의 네이밍 규칙
- ARCHITECTURE.md의 레이어 구조
- API_DESIGN.md의 Response DTO 패턴"
```

### 3. 단계별 진행
복잡한 작업은 **단계별로 나누어** 요청하세요.

```
예시: Judge0 연동 구현

단계 1: "Judge0SubmissionRequest DTO를 작성해주세요"
       → 검토 및 확인

단계 2: "Judge0Client 클래스를 구현해주세요"
       → 검토 및 확인

단계 3: "SubmissionService에 채점 로직을 추가해주세요"
       → 검토 및 확인
```

## 📝 AI 작업 템플릿

### 템플릿 1: API 추가

```
작업: {기능명} API 구현

참고 문서:
- docs/ai/API_DESIGN.md
- docs/ai/CONVENTIONS.md
- docs/ai/ARCHITECTURE.md

요구사항:
1. Request DTO 작성 (Validation 포함)
2. Response DTO 작성 (Builder 패턴)
3. Controller 메서드 작성
4. Service 비즈니스 로직 구현
5. Repository 쿼리 메서드 (필요 시)
6. Swagger 어노테이션 추가

검증 사항:
- [ ] 네이밍 규칙 준수
- [ ] 레이어 구조 준수
- [ ] DTO 패턴 준수
- [ ] Swagger 문서화 완료
```

### 템플릿 2: Entity 추가

```
작업: {Entity명} 엔티티 추가

참고 문서:
- docs/ai/DATABASE.md
- docs/ai/CONVENTIONS.md

요구사항:
1. BaseTimeEntity 상속
2. Builder 패턴 적용
3. 연관관계 매핑 (필요 시)
4. 인덱스 설정
5. 비즈니스 메서드 작성

검증 사항:
- [ ] Setter 사용하지 않음
- [ ] FetchType.LAZY 설정
- [ ] Enum은 EnumType.STRING
- [ ] NOT NULL, UNIQUE 제약 설정
```

### 템플릿 3: Judge0 관련 작업

```
작업: {기능명} Judge0 연동

참고 문서:
- docs/ai/JUDGE0.md
- docs/ai/CONVENTIONS.md

요구사항:
1. Judge0 API 호출 구현
2. Status 매핑 처리
3. 에러 처리 및 재시도 로직
4. 결과 저장 로직

검증 사항:
- [ ] Judge0LanguageId enum 사용
- [ ] Status ID 매핑 정확
- [ ] 예외 처리 완료
- [ ] 타임아웃 설정 확인
```

## 🔍 코드 리뷰 체크리스트

AI가 생성한 코드를 리뷰할 때 다음 사항을 확인하세요:

### 전체 공통
- [ ] docs/ai/ 문서의 규칙 준수
- [ ] 기존 코드와 일관성 유지
- [ ] 패키지 구조 준수
- [ ] 네이밍 규칙 준수 (camelCase, PascalCase)

### Controller
- [ ] Service만 호출 (Repository 직접 호출 금지)
- [ ] DTO로만 통신 (Entity 직접 반환 금지)
- [ ] HTTP 메서드 적절히 사용
- [ ] Swagger 어노테이션 추가

### Service
- [ ] 비즈니스 로직 집중
- [ ] @Transactional 적절히 사용
- [ ] Entity ↔ DTO 변환 처리
- [ ] 예외 처리 명확

### Entity
- [ ] Setter 사용 안 함
- [ ] FetchType.LAZY 설정
- [ ] Builder 패턴 적용
- [ ] 비즈니스 메서드 포함

### DTO
- [ ] Request: Validation 어노테이션
- [ ] Response: Builder + Getter (불변)
- [ ] 정적 팩토리 메서드 (from, of)

## 🎓 AI 활용 팁

### 1. 기존 코드 참조 요청
```
"ProblemController.java를 참고하여 SubmissionController를 작성해주세요"
```

### 2. 패턴 학습 요청
```
"프로젝트의 Service 클래스들을 분석하고 동일한 패턴으로
UserService를 작성해주세요"
```

### 3. 문서 기반 검증 요청
```
"방금 작성한 코드가 docs/ai/CONVENTIONS.md의
Java 21 기능 활용 가이드를 준수하는지 확인해주세요"
```

### 4. 리팩토리ng 요청
```
"이 코드를 docs/ai/ARCHITECTURE.md의 레이어 구조에 맞게
리팩토링해주세요"
```

## 📊 작업 효율성 향상

### 일괄 작업 요청
```
"다음 작업을 순서대로 수행해주세요:

1. docs/ai/DATABASE.md를 참고하여 Contest 엔티티 생성
2. ContestRepository 인터페이스 생성
3. docs/ai/API_DESIGN.md를 참고하여 Contest CRUD API 구현
4. Swagger 문서화

각 단계가 완료되면 다음 단계로 진행해주세요."
```

### 컨텍스트 제공
```
"현재 Judge0 비동기 채점 기능을 구현 중입니다.
docs/ai/JUDGE0.md의 '비동기 방식' 섹션을 참고하여
폴링 로직을 구현해주세요."
```

## 🚨 주의사항

### 1. AI는 완벽하지 않습니다
- AI가 생성한 코드는 **반드시 리뷰**하세요
- 문서와 다른 내용이 있으면 **문서를 우선**하세요
- 이상한 코드가 있으면 **즉시 수정** 요청하세요

### 2. 보안 고려
- 민감한 정보(비밀번호, API 키)를 코드에 하드코딩하지 마세요
- 환경 변수나 설정 파일 사용

### 3. 테스트 코드
- 중요한 비즈니스 로직은 **테스트 코드 작성** 요청
- AI에게 Given-When-Then 패턴으로 테스트 작성 요청

## ✅ 체크리스트 요약

AI에게 작업 요청 전:
- [ ] 관련 문서 확인 (API_DESIGN, CONVENTIONS 등)
- [ ] WORK_STATUS.md 업데이트 (TODO → IN_PROGRESS)
- [ ] 작업 범위 명확히 정의
- [ ] 참조할 문서 AI에게 명시

AI 작업 완료 후:
- [ ] 문서 규칙 준수 확인
- [ ] 기존 코드와 일관성 확인
- [ ] 테스트 실행 (필요 시)
- [ ] WORK_STATUS.md 업데이트 (DONE)
- [ ] Git 커밋

## 📝 변경 이력

- 2025-12-05: AI 작업 규칙 문서 작성
- 2025-12-09: AI 응답 최적화 규칙 추가 (이모지 금지, 리스트 금지, 최소 1000자)
