package com.okestro.okestroonlinejudge.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * MinIO 버킷 초기화 컴포넌트.
 * 애플리케이션 시작 시 필요한 버킷들을 자동으로 생성합니다.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioInitializer implements CommandLineRunner {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.image-bucket-name:okestro-images}")
    private String imageBucketName;

    @Override
    public void run(String... args) {
        createBucketIfNotExists(bucketName);
        createBucketIfNotExists(imageBucketName);
        createBucketIfNotExists("test-cases"); // 테스트케이스용 버킷
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("✅ MinIO 버킷 생성 완료: {}", bucketName);
            } else {
                log.info("✓ MinIO 버킷 이미 존재: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("❌ MinIO 버킷 생성 실패: {} - {}", bucketName, e.getMessage());
            log.warn("⚠️ MinIO 서버가 실행 중인지 확인하세요. URL: {}", getMinioUrl());
        }
    }

    private String getMinioUrl() {
        try {
            return minioClient.toString();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}

