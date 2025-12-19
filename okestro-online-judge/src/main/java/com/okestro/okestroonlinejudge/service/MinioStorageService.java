package com.okestro.okestroonlinejudge.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * MinIO를 이용한 파일 저장소 서비스 구현체.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;

    @Override
    public String upload(MultipartFile file, String bucketName) {
        try {
            ensureBucketExists(bucketName);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            log.error("MinIO upload failed", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public String uploadString(String content, String fileName, String bucketName) {
        try {
            ensureBucketExists(bucketName);
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream bais = new ByteArrayInputStream(contentBytes);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(bais, contentBytes.length, -1)
                            .contentType("text/markdown")
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            log.error("MinIO string upload failed", e);
            throw new RuntimeException("String upload failed", e);
        }
    }

    @Override
    public InputStream download(String fileName, String bucketName) {
        try {
            ensureBucketExists(bucketName);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO download failed", e);
            throw new RuntimeException("File download failed", e);
        }
    }

    @Override
    public String readString(String fileName, String bucketName) {
        try (InputStream stream = download(fileName, bucketName)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("MinIO read string failed", e);
            throw new RuntimeException("Read string failed", e);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Bucket check/creation failed", e);
            throw new RuntimeException("Bucket operation failed", e);
        }
    }
}


