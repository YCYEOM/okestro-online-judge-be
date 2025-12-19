package com.okestro.okestroonlinejudge.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 이미지 관리 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioClient minioClient;

    @Value("${minio.image-bucket-name:okestro-images}")
    private String imageBucketName;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 이미지를 업로드한다.
     *
     * @param file 업로드할 이미지 파일
     * @return 저장된 파일명
     */
    public String uploadImage(MultipartFile file) {
        validateImage(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID() + extension;

        try {
            ensureBucketExists(imageBucketName);

            ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(imageBucketName)
                            .object(fileName)
                            .stream(bais, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Image uploaded successfully: {}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("Image upload failed", e);
            throw new RuntimeException("Image upload failed", e);
        }
    }

    /**
     * 이미지를 다운로드한다.
     *
     * @param fileName 파일명
     * @return 이미지 InputStream
     */
    public InputStream downloadImage(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(imageBucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Image download failed: {}", fileName, e);
            throw new RuntimeException("Image download failed", e);
        }
    }

    /**
     * 이미지의 Content-Type을 추론한다.
     *
     * @param fileName 파일명
     * @return Content-Type
     */
    public String getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 이미지 파일을 검증한다.
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (JPEG, PNG, GIF, WebP만 지원)");
        }
    }

    /**
     * 파일 확장자를 추출한다.
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".png";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 버킷이 존재하는지 확인하고, 없으면 생성한다.
     */
    private void ensureBucketExists(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Bucket check/creation failed", e);
            throw new RuntimeException("Bucket operation failed", e);
        }
    }
}
