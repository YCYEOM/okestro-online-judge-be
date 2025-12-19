package com.okestro.okestroonlinejudge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 제출 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitCodeRequest {

    private Long problemId;
    private String language;
    private String sourceCode;
    private Long userId; // TODO: SecurityContext에서 가져오도록 변경 필요
}


