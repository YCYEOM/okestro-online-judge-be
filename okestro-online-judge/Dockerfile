# ────────────────────────────────
# 📦 Build Stage: Gradle 빌드
# ────────────────────────────────
FROM gradle:8.5-jdk21-alpine AS build-stage

WORKDIR /app

# Gradle 캐싱을 위해 의존성 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (캐싱 활용)
RUN gradle dependencies --no-daemon || true

# 소스 복사 및 빌드
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ────────────────────────────────
# 🚀 Production Stage: JRE 실행
# ────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 보안을 위한 non-root 사용자 생성
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -D appuser

# 빌드된 JAR 복사
COPY --from=build-stage /app/build/libs/*.jar app.jar

# 권한 설정
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
