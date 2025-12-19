package com.okestro.okestroonlinejudge.dto.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Judge0 API 제출 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0SubmissionResponse {

    private String token;

    private String stdout;

    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;

    private String time;

    private Integer memory;

    private Judge0Status status;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Judge0Status {
        private Integer id;
        private String description;
    }
}
