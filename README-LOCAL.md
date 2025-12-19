# 로컬 개발 환경 실행 가이드

## 1. 사전 요구사항
- Docker Desktop (또는 Docker Engine)
- Java 21 JDK
- Node.js & npm (또는 pnpm)

## 2. 백엔드 (okestro-online-judge-be)

### 설정
`okestro-online-judge/src/main/resources/application-local.yml` 파일이 생성되었습니다. 이 설정은 로컬 Docker 서비스를 가리킵니다.

### 실행 방법
1. **Docker 서비스 시작**
   ```bash
   # 프로젝트 루트(okestro-online-judge-be)에서 실행
   docker-compose -f docker-compose.yml -f docker-compose.judge0.yml up -d
   ```
   (MariaDB, MinIO, Judge0 서비스가 실행됩니다.)

2. **애플리케이션 실행**
   ```bash
   cd okestro-online-judge
   # Windows
   .\gradlew bootRun --args='--spring.profiles.active=local'
   # Mac/Linux
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

## 3. 프론트엔드 (okestro-online-judge-fe)

### 설정
`fe-env-setup.txt` 파일의 내용을 `.env.local` 파일로 이름을 변경하거나 복사하세요.
(이 파일은 API 서버 주소를 `http://localhost:8080`으로 설정합니다.)

### 실행 방법
1. **의존성 설치**
   ```bash
   npm install
   # 또는
   pnpm install
   ```

2. **개발 서버 실행**
   ```bash
   npm run dev
   # 또는
   pnpm dev
   ```

## 참고사항
- HTTPS 인증서(`cert/` 폴더)가 없으면 HTTP로 실행되도록 설정이 수정되었습니다.
- Judge0, MinIO, MariaDB가 Docker에서 정상적으로 실행 중이어야 백엔드가 오류 없이 시작됩니다.

