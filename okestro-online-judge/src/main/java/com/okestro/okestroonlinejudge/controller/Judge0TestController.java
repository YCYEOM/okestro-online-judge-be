package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.client.Judge0Client;
import com.okestro.okestroonlinejudge.dto.judge0.Judge0Language;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Judge0 API 테스트 컨트롤러.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/judge0/test")
@Tag(name = "Judge0 Test", description = "Judge0 API 테스트 엔드포인트")
public class Judge0TestController {

    private final Judge0Client judge0Client;

    @Operation(summary = "서버 상태 확인", description = "Judge0 서버 연결 상태를 확인합니다.")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkStatus() {
        Map<String, Object> response = new HashMap<>();
        boolean isConnected = judge0Client.checkServerStatus();
        response.put("connected", isConnected);
        response.put("message", isConnected ? "Judge0 서버 연결 성공" : "Judge0 서버 연결 실패");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원 언어 목록", description = "지원하는 프로그래밍 언어 목록을 반환합니다.")
    @GetMapping("/languages")
    public ResponseEntity<List<Map<String, Object>>> getLanguages() {
        List<Map<String, Object>> languages = Arrays.stream(Judge0Language.values())
                .map(lang -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", lang.getLanguageName());
                    map.put("id", lang.getLanguageId());
                    map.put("description", lang.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(languages);
    }

    @Operation(summary = "코드 채점", description = "코드를 제출하고 채점 결과를 반환합니다.")
    @PostMapping("/judge")
    public ResponseEntity<Map<String, Object>> judgeCode(@RequestBody JudgeRequest request) {
        log.info("채점 요청: language={}", request.language);

        Judge0Client.JudgeResult result = judge0Client.judge(
                request.sourceCode,
                request.language,
                request.stdin,
                request.expectedOutput
        );

        Map<String, Object> response = new HashMap<>();
        response.put("result", result.getResult().name());
        response.put("accepted", result.isAccepted());
        response.put("stdout", result.getStdout());
        response.put("stderr", result.getStderr());
        response.put("compileOutput", result.getCompileOutput());
        response.put("executionTime", result.getExecutionTime());
        response.put("memoryUsage", result.getMemoryUsage());
        response.put("statusDescription", result.getStatusDescription());
        response.put("errorMessage", result.getErrorMessage());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "테스트 케이스 실행", description = "미리 정의된 테스트 케이스들을 실행합니다.")
    @PostMapping("/run-tests")
    public ResponseEntity<Map<String, Object>> runTests() {
        Map<String, Object> results = new HashMap<>();
        int passed = 0;
        int failed = 0;

        // 테스트 케이스 1: Python Hello World
        JudgeRequest test1 = new JudgeRequest();
        test1.language = "python";
        test1.sourceCode = "print(\"Hello, World!\")";
        test1.stdin = "";
        test1.expectedOutput = "Hello, World!\n";
        Judge0Client.JudgeResult result1 = judge0Client.judge(test1.sourceCode, test1.language, test1.stdin, test1.expectedOutput);
        results.put("test1_python_hello", Map.of(
                "name", "Python Hello World",
                "result", result1.getResult().name(),
                "passed", result1.isAccepted()
        ));
        if (result1.isAccepted()) passed++; else failed++;

        // 테스트 케이스 2: Python 두 수의 합
        JudgeRequest test2 = new JudgeRequest();
        test2.language = "python";
        test2.sourceCode = "a, b = map(int, input().split())\nprint(a + b)";
        test2.stdin = "3 5";
        test2.expectedOutput = "8\n";
        Judge0Client.JudgeResult result2 = judge0Client.judge(test2.sourceCode, test2.language, test2.stdin, test2.expectedOutput);
        results.put("test2_python_sum", Map.of(
                "name", "Python 두 수의 합",
                "result", result2.getResult().name(),
                "passed", result2.isAccepted()
        ));
        if (result2.isAccepted()) passed++; else failed++;

        // 테스트 케이스 3: Java Hello World
        JudgeRequest test3 = new JudgeRequest();
        test3.language = "java";
        test3.sourceCode = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;
        test3.stdin = "";
        test3.expectedOutput = "Hello, World!\n";
        Judge0Client.JudgeResult result3 = judge0Client.judge(test3.sourceCode, test3.language, test3.stdin, test3.expectedOutput);
        results.put("test3_java_hello", Map.of(
                "name", "Java Hello World",
                "result", result3.getResult().name(),
                "passed", result3.isAccepted()
        ));
        if (result3.isAccepted()) passed++; else failed++;

        // 테스트 케이스 4: C Hello World
        JudgeRequest test4 = new JudgeRequest();
        test4.language = "c";
        test4.sourceCode = """
                #include <stdio.h>

                int main() {
                    printf("Hello, World!\\n");
                    return 0;
                }
                """;
        test4.stdin = "";
        test4.expectedOutput = "Hello, World!\n";
        Judge0Client.JudgeResult result4 = judge0Client.judge(test4.sourceCode, test4.language, test4.stdin, test4.expectedOutput);
        results.put("test4_c_hello", Map.of(
                "name", "C Hello World",
                "result", result4.getResult().name(),
                "passed", result4.isAccepted()
        ));
        if (result4.isAccepted()) passed++; else failed++;

        // 테스트 케이스 5: 오답 테스트 (일부러 틀린 답)
        JudgeRequest test5 = new JudgeRequest();
        test5.language = "python";
        test5.sourceCode = "print(\"Wrong Answer\")";
        test5.stdin = "";
        test5.expectedOutput = "Hello, World!\n";
        Judge0Client.JudgeResult result5 = judge0Client.judge(test5.sourceCode, test5.language, test5.stdin, test5.expectedOutput);
        boolean expectedWrong = result5.getResult().name().equals("WRONG_ANSWER");
        results.put("test5_wrong_answer", Map.of(
                "name", "오답 테스트 (WRONG_ANSWER 예상)",
                "result", result5.getResult().name(),
                "passed", expectedWrong
        ));
        if (expectedWrong) passed++; else failed++;

        // 테스트 케이스 6: 런타임 에러 테스트
        JudgeRequest test6 = new JudgeRequest();
        test6.language = "python";
        test6.sourceCode = "x = 1 / 0";
        test6.stdin = "";
        test6.expectedOutput = "";
        Judge0Client.JudgeResult result6 = judge0Client.judge(test6.sourceCode, test6.language, test6.stdin, test6.expectedOutput);
        boolean expectedRuntime = result6.getResult().name().equals("RUNTIME_ERROR");
        results.put("test6_runtime_error", Map.of(
                "name", "런타임 에러 테스트 (RUNTIME_ERROR 예상)",
                "result", result6.getResult().name(),
                "passed", expectedRuntime
        ));
        if (expectedRuntime) passed++; else failed++;

        // 요약
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", passed + failed);
        summary.put("passed", passed);
        summary.put("failed", failed);
        summary.put("results", results);

        return ResponseEntity.ok(summary);
    }

    public static class JudgeRequest {
        public String sourceCode;
        public String language;
        public String stdin;
        public String expectedOutput;
    }
}
