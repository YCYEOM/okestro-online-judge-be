package com.okestro.okestroonlinejudge.dto.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Judge0 API 제출 요청 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0SubmissionRequest {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("cpu_time_limit")
    private Double cpuTimeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;

    @JsonProperty("max_file_size")
    private Integer maxFileSize;
}
