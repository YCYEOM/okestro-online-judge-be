# BE-FE 인증 시스템 구현 계획

> 사용자 인증부터 FE 연동까지의 단계별 구현 가이드
> 작성일: 2025-12-05

---

## 📊 현재 상태

### BE 현황
- ✅ Spring Boot 3.4.0 + Java 21
- ✅ Spring Security 의존성 있음 (설정 필요)
- ✅ UserEntity 존재 (username, email, passwordHash, role)
- ✅ MariaDB 연결 완료
- ❌ JWT 의존성 없음
- ❌ 인증 API 없음

### FE 현황 (이미 구현됨)
- ✅ 로그인 페이지 (`/auth/login`)
- ✅ 회원가입 페이지 (`/auth/signup`)
- ✅ 비밀번호 찾기 페이지 (`/auth/password-recovery`)
- ✅ OAuth2 토큰 API 호출 (`POST /oauth2/token`)
- ✅ axios interceptor (토큰 자동 갱신)

---

## 🎯 구현 단계 (우선순위 순)

### Phase 1: 기본 인증 (필수 - FE 로그인 연동)

#### Step 1.1: JWT 의존성 추가
```gradle
// build.gradle에 추가
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```

#### Step 1.2: Spring Security 설정 (BE-012)
**파일**: `config/SecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능
                .requestMatchers("/oauth2/token").permitAll()
                .requestMatchers("/u/auth/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/v1/problems/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### Step 1.3: JWT 유틸리티 (BE-001, BE-002)
**파일**: `security/JwtTokenProvider.java`
- Access Token 생성 (30분)
- Refresh Token 생성 (7일)
- 토큰 검증
- 토큰에서 사용자 정보 추출

#### Step 1.4: 로그인 API (BE-001)
**엔드포인트**: `POST /oauth2/token`

**FE 요청 형식**:
```
Content-Type: application/x-www-form-urlencoded
Authorization: Basic {base64(client_id:client_secret)}

grant_type=password&username={email}&password={password}
```

**BE 응답 형식** (FE에서 기대하는 형식):
```json
{
  "access_token": "eyJhbG...",
  "refresh_token": "eyJhbG...",
  "token_type": "Bearer",
  "expires_in": 1800
}
```

#### Step 1.5: 토큰 갱신 API (BE-002)
**엔드포인트**: `POST /oauth2/token`

**FE 요청 형식**:
```
grant_type=refresh_token&refresh_token={refresh_token}
```

---

### Phase 2: 회원가입 (FE 회원가입 연동)

#### Step 2.1: 회원가입 API (BE-005)
**엔드포인트**: `POST /u/auth/sign-up`

**FE 요청 형식**:
```json
{
  "userName": "홍길동",
  "email": "user@example.com",
  "password": "Password123!",
  "groupName": "오케스트로"
}
```

**BE 응답 형식**:
```json
{
  "responseTime": "2025-12-05T10:00:00",
  "errorMessage": "",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "userName": "홍길동"
  },
  "status": 201
}
```

#### Step 2.2: 닉네임(사용자명) 중복 확인 (BE-007)
**엔드포인트**: `GET /auth/check-nickname?nickname={nickname}`

**BE 응답**:
```json
{
  "responseTime": "...",
  "errorMessage": "",
  "data": {
    "available": true
  },
  "status": 200
}
```

---

### Phase 3: 이메일 인증 (선택 - 나중에 구현 가능)

#### Step 3.1: 이메일 인증 코드 발송 (BE-003)
**엔드포인트**: `POST /u/auth/send-code`
- SMTP 서버 연동 필요
- 6자리 인증 코드 생성
- Redis에 코드 저장 (5분 TTL)

#### Step 3.2: 이메일 인증 코드 검증 (BE-004)
**엔드포인트**: `POST /u/auth/verify-code`

---

### Phase 4: 사용자 정보 (FE 프로필 연동)

#### Step 4.1: 현재 사용자 정보 조회 (BE-009)
**엔드포인트**: `GET /u/users/me`

**BE 응답**:
```json
{
  "responseTime": "...",
  "errorMessage": "",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "userName": "홍길동",
    "groupName": "오케스트로",
    "role": "USER",
    "profileImage": null
  },
  "status": 200
}
```

#### Step 4.2: 사용자 정보 수정 (BE-010)
**엔드포인트**: `PATCH /u/users/me`

---

## 📁 구현할 파일 목록

### 1. 설정 파일
```
src/main/java/com/okestro/okestroonlinejudge/
├── config/
│   ├── SecurityConfig.java          # Spring Security 설정
│   ├── CorsConfig.java               # CORS 설정
│   └── JwtConfig.java                # JWT 설정값
```

### 2. 보안 관련
```
├── security/
│   ├── JwtTokenProvider.java         # JWT 생성/검증
│   ├── JwtAuthenticationFilter.java  # JWT 인증 필터
│   ├── CustomUserDetails.java        # UserDetails 구현
│   └── CustomUserDetailsService.java # UserDetailsService 구현
```

### 3. 인증 API
```
├── controller/
│   ├── AuthController.java           # /oauth2/token, /u/auth/*
│   └── UserController.java           # /u/users/*
├── service/
│   ├── AuthService.java              # 인증 비즈니스 로직
│   └── UserService.java              # 사용자 비즈니스 로직
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── SignUpRequest.java
│   │   └── TokenRefreshRequest.java
│   └── response/
│       ├── TokenResponse.java
│       └── UserResponse.java
```

---

## 🚀 빠른 시작 (MVP)

**최소 구현으로 FE 연동하기**:

1. **JWT 의존성 추가** (5분)
2. **SecurityConfig 설정** - 모든 요청 permitAll로 시작 (10분)
3. **AuthController 생성** - POST /oauth2/token (30분)
4. **JwtTokenProvider 생성** - 토큰 생성/검증 (30분)
5. **FE 테스트** - 로그인 시도 (10분)

**예상 소요 시간**: 약 1~2시간

---

## ⚠️ 주의사항

### FE에서 기대하는 응답 형식
FE는 OAuth2 표준 응답을 기대합니다:
```json
{
  "access_token": "...",
  "refresh_token": "...",
  "token_type": "Bearer",
  "expires_in": 1800
}
```
**주의**: `accessToken`이 아닌 `access_token` (snake_case)

### CORS 설정 필수
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173")); // FE 주소
    config.setAllowedMethods(List.of("*"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### 이메일 인증 우회 (개발 단계)
초기에는 이메일 인증 없이 바로 회원가입 가능하도록 구현 후,
나중에 SMTP 연동 시 활성화

---

## 📋 작업 체크리스트

- [ ] **BE-012**: Spring Security 설정
- [ ] **BE-001**: OAuth2 토큰 발급 (로그인)
- [ ] **BE-002**: 토큰 갱신 (Refresh Token)
- [ ] **BE-005**: 회원가입 API
- [ ] **BE-007**: 닉네임 중복 확인
- [ ] **BE-009**: 현재 사용자 정보 조회
- [ ] **BE-010**: 사용자 정보 수정
- [ ] **BE-003**: 이메일 인증 코드 발송 (선택)
- [ ] **BE-004**: 이메일 인증 코드 검증 (선택)
- [ ] **BE-006**: 비밀번호 찾기 (선택)
- [ ] **BE-008**: 약관 조회 (선택)
- [ ] **BE-011**: 회원 탈퇴 (선택)

---

## 🔗 참고

- [FE auth-api.tsx](../../okestro-online-judge-fe/src/api/auth-api.tsx)
- [FE auth.d.ts](../../okestro-online-judge-fe/src/@types/auth.d.ts)
- [BE UserEntity](../okestro-online-judge/src/main/java/com/okestro/okestroonlinejudge/domain/UserEntity.java)
