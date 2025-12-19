package com.okestro.okestroonlinejudge.client;

import com.okestro.okestroonlinejudge.config.Judge0Config;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import com.okestro.okestroonlinejudge.dto.judge0.Judge0Language;
import com.okestro.okestroonlinejudge.dto.judge0.Judge0StatusId;
import com.okestro.okestroonlinejudge.dto.judge0.Judge0SubmissionRequest;
import com.okestro.okestroonlinejudge.dto.judge0.Judge0SubmissionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Judge0 API 클라이언트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Judge0Client {

    private final RestTemplate restTemplate;
    private final Judge0Config judge0Config;

    /**
     * 코드를 제출하고 채점 결과를 반환합니다.
     *
     * @param sourceCode 소스 코드
     * @param language   언어 (예: python, java, c, cpp)
     * @param stdin      표준 입력
     * @param expectedOutput 예상 출력
     * @return 채점 결과
     */
    public JudgeResult judge(String sourceCode, String language, String stdin, String expectedOutput) {
        Integer languageId = Judge0Language.getLanguageId(language);
        if (languageId == null) {
            log.error("지원하지 않는 언어: {}", language);
            return JudgeResult.error("지원하지 않는 언어입니다: " + language);
        }

        String token = submitCode(sourceCode, languageId, stdin, expectedOutput);
        if (token == null) {
            return JudgeResult.error("코드 제출에 실패했습니다.");
        }

        return pollResult(token);
    }

    /**
     * 서버 상태를 확인합니다.
     *
     * @return 서버 연결 성공 여부
     */
    public boolean checkServerStatus() {
        try {
            String url = judge0Config.getApiUrl() + "/about";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            boolean isOk = response.getStatusCode().is2xxSuccessful();
            if (isOk) {
                log.info("Judge0 서버 연결 성공: {}", judge0Config.getApiUrl());
            }
            return isOk;
        } catch (RestClientException e) {
            log.error("Judge0 서버 연결 실패: {}", e.getMessage());
            return false;
        }
    }

    private String submitCode(String sourceCode, Integer languageId, String stdin, String expectedOutput) {
        try {
            String url = judge0Config.getApiUrl() + "/submissions?base64_encoded=true&wait=false";

            Judge0SubmissionRequest request = Judge0SubmissionRequest.builder()
                    .sourceCode(encodeBase64(sourceCode))
                    .languageId(languageId)
                    .stdin(stdin != null ? encodeBase64(stdin) : "")
                    .expectedOutput(expectedOutput != null ? encodeBase64(expectedOutput) : "")
                    .cpuTimeLimit(judge0Config.getCpuTimeLimit())
                    .memoryLimit(judge0Config.getMemoryLimit())
                    .maxFileSize(judge0Config.getMaxFileSize())
                    .build();

            HttpHeaders headers = createAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Judge0SubmissionRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Judge0SubmissionResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Judge0SubmissionResponse.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String token = response.getBody().getToken();
                log.info("코드 제출 완료: token={}", token);
                return token;
            }

            log.error("코드 제출 실패: status={}", response.getStatusCode());
            return null;
        } catch (RestClientException e) {
            log.error("코드 제출 요청 실패: {}", e.getMessage());
            return null;
        }
    }

    private JudgeResult pollResult(String token) {
        String url = judge0Config.getApiUrl() + "/submissions/" + token + "?base64_encoded=true&fields=*";
        int maxRetries = judge0Config.getMaxRetries();
        int retryDelay = judge0Config.getRetryDelayMs();

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
                ResponseEntity<Judge0SubmissionResponse> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, Judge0SubmissionResponse.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Judge0SubmissionResponse result = response.getBody();
                    int statusId = result.getStatus() != null ? result.getStatus().getId() : 0;

                    if (Judge0StatusId.isCompleted(statusId)) {
                        return toJudgeResult(result);
                    }

                    log.debug("채점 대기 중... (상태: {})",
                            result.getStatus() != null ? result.getStatus().getDescription() : "Unknown");
                }

                Thread.sleep(retryDelay);
            } catch (RestClientException e) {
                log.error("결과 조회 실패: {}", e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return JudgeResult.error("채점이 중단되었습니다.");
            }
        }

        return JudgeResult.error("채점 시간 초과");
    }

    private JudgeResult toJudgeResult(Judge0SubmissionResponse response) {
        int statusId = response.getStatus() != null ? response.getStatus().getId() : 0;
        SubmissionResult submissionResult = Judge0StatusId.toSubmissionResult(statusId);

        String stdout = decodeBase64(response.getStdout());
        String stderr = decodeBase64(response.getStderr());
        String compileOutput = decodeBase64(response.getCompileOutput());
        String message = decodeBase64(response.getMessage());

        // 디버그 로깅 추가
        log.info("Judge0 결과 - statusId: {}, status: {}", statusId,
                response.getStatus() != null ? response.getStatus().getDescription() : "null");
        if (stderr != null && !stderr.isEmpty()) {
            log.info("Judge0 stderr: {}", stderr);
        }
        if (compileOutput != null && !compileOutput.isEmpty()) {
            log.info("Judge0 compileOutput: {}", compileOutput);
        }
        if (message != null && !message.isEmpty()) {
            log.info("Judge0 message: {}", message);
        }
        log.info("Judge0 stdout: {}", stdout);

        Double time = null;
        if (response.getTime() != null) {
            try {
                time = Double.parseDouble(response.getTime());
            } catch (NumberFormatException ignored) {
            }
        }

        return JudgeResult.builder()
                .result(submissionResult)
                .stdout(stdout)
                .stderr(stderr)
                .compileOutput(compileOutput)
                .message(message)
                .executionTime(time)
                .memoryUsage(response.getMemory())
                .statusDescription(response.getStatus() != null ? response.getStatus().getDescription() : null)
                .build();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (judge0Config.getAuthnToken() != null && !judge0Config.getAuthnToken().isEmpty()) {
            headers.set("X-Auth-Token", judge0Config.getAuthnToken());
        }
        if (judge0Config.getAuthzToken() != null && !judge0Config.getAuthzToken().isEmpty()) {
            headers.set("X-Auth-User", judge0Config.getAuthzToken());
        }
        return headers;
    }

    private String encodeBase64(String text) {
        if (text == null) return "";
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeBase64(String text) {
        if (text == null || text.isEmpty()) return "";
        try {
            // Judge0 응답에서 줄바꿈이 포함될 수 있으므로 trim 처리
            String trimmed = text.trim();
            return new String(Base64.getDecoder().decode(trimmed), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return text;
        }
    }

    /**
     * 채점 결과 DTO.
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class JudgeResult {
        private final SubmissionResult result;
        private final String stdout;
        private final String stderr;
        private final String compileOutput;
        private final String message;
        private final Double executionTime;
        private final Integer memoryUsage;
        private final String statusDescription;
        private final String errorMessage;

        public static JudgeResult error(String message) {
            return JudgeResult.builder()
                    .result(SubmissionResult.RUNTIME_ERROR)
                    .errorMessage(message)
                    .build();
        }

        public boolean isAccepted() {
            return result == SubmissionResult.ACCEPTED;
        }
    }
}
