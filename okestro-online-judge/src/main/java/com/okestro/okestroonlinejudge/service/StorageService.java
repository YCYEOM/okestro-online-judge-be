package com.okestro.okestroonlinejudge.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 파일 저장소 서비스 인터페이스.
 *
 * @author Assistant
 * @since 1.0
 */
public interface StorageService {

    /**
     * 파일을 업로드한다.
     *
     * @param file 업로드할 파일
     * @param bucketName 버킷 이름
     * @return 저장된 파일 경로 (또는 파일명)
     */
    String upload(MultipartFile file, String bucketName);

    /**
     * 내용을 문자열로 업로드한다. (마크다운 등)
     *
     * @param content 내용
     * @param fileName 파일명
     * @param bucketName 버킷 이름
     * @return 저장된 파일 경로
     */
    String uploadString(String content, String fileName, String bucketName);

    /**
     * 파일을 다운로드(InputStream)한다.
     *
     * @param fileName 파일명
     * @param bucketName 버킷 이름
     * @return 파일 InputStream
     */
    InputStream download(String fileName, String bucketName);

    /**
     * 파일 내용을 문자열로 읽어온다.
     *
     * @param fileName 파일명
     * @param bucketName 버킷 이름
     * @return 파일 내용 문자열
     */
    String readString(String fileName, String bucketName);
}


