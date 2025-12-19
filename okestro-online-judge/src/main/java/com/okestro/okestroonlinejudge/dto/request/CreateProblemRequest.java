package com.okestro.okestroonlinejudge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 생성 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProblemRequest {

    private String title;
    private String content; // Markdown content directly
    private String inputDesc;
    private String outputDesc;
    private Integer difficulty;
    private Integer timeLimitMs;
    private Integer memoryLimitKb;
}


