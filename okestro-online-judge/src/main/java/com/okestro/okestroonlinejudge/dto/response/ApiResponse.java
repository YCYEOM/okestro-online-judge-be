package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 공통 응답 DTO.
 *
 * FE utils.d.ts의 ApiResponse 타입과 일치:
 * {
 *   responseTime: string;
 *   errorMessage: string;
 *   data: T;
 *   status: number;
 * }
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String responseTime;

    private String errorMessage;

    private T data;

    private int status;

    /**
     * 성공 응답 생성.
     *
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .responseTime(LocalDateTime.now().toString())
                .errorMessage("")
                .data(data)
                .status(200)
                .build();
    }

    /**
     * 성공 응답 생성 (상태 코드 지정).
     *
     * @param data   응답 데이터
     * @param status HTTP 상태 코드
     * @param <T>    데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data, int status) {
        return ApiResponse.<T>builder()
                .responseTime(LocalDateTime.now().toString())
                .errorMessage("")
                .data(data)
                .status(status)
                .build();
    }

    /**
     * 에러 응답 생성.
     *
     * @param errorMessage 에러 메시지
     * @param status       HTTP 상태 코드
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> error(String errorMessage, int status) {
        return ApiResponse.<T>builder()
                .responseTime(LocalDateTime.now().toString())
                .errorMessage(errorMessage)
                .data(null)
                .status(status)
                .build();
    }

    /**
     * 에러 응답 생성 (400 Bad Request).
     *
     * @param errorMessage 에러 메시지
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> badRequest(String errorMessage) {
        return error(errorMessage, 400);
    }

    /**
     * 에러 응답 생성 (404 Not Found).
     *
     * @param errorMessage 에러 메시지
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> notFound(String errorMessage) {
        return error(errorMessage, 404);
    }

    /**
     * 에러 응답 생성 (401 Unauthorized).
     *
     * @param errorMessage 에러 메시지
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> unauthorized(String errorMessage) {
        return error(errorMessage, 401);
    }

    /**
     * 에러 응답 생성 (403 Forbidden).
     *
     * @param errorMessage 에러 메시지
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> forbidden(String errorMessage) {
        return error(errorMessage, 403);
    }

    /**
     * 에러 응답 생성 (500 Internal Server Error).
     *
     * @param errorMessage 에러 메시지
     * @param <T>          데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> internalError(String errorMessage) {
        return error(errorMessage, 500);
    }
}
