package com.okestro.okestroonlinejudge.client;

import com.okestro.okestroonlinejudge.config.Judge0Config;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Judge0 API 클라이언트 통합 테스트.
 *
 * 실행 전 Judge0 서버가 실행 중이어야 합니다.
 * application-test.yml의 judge0.api-url을 실제 서버 주소로 설정하세요.
 */
@DisplayName("Judge0 Client 통합 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Judge0ClientTest {

    private Judge0Client judge0Client;
    private Judge0Config judge0Config;

    @BeforeEach
    void setUp() {
        judge0Config = loadConfigFromYaml();
        RestTemplate restTemplate = new RestTemplate();
        judge0Client = new Judge0Client(restTemplate, judge0Config);
    }

    private Judge0Config loadConfigFromYaml() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application-test.yml")) {
            if (inputStream == null) {
                throw new RuntimeException("application-test.yml not found");
            }
            Map<String, Object> config = yaml.load(inputStream);
            @SuppressWarnings("unchecked")
            Map<String, Object> judge0 = (Map<String, Object>) config.get("judge0");

            Judge0Config judge0Config = new Judge0Config();
            judge0Config.setApiUrl((String) judge0.get("api-url"));
            judge0Config.setAuthnToken((String) judge0.get("authn-token"));
            judge0Config.setAuthzToken((String) judge0.get("authz-token"));
            judge0Config.setCpuTimeLimit(((Number) judge0.get("cpu-time-limit")).doubleValue());
            judge0Config.setMemoryLimit(((Number) judge0.get("memory-limit")).intValue());
            judge0Config.setMaxRetries(((Number) judge0.get("max-retries")).intValue());
            judge0Config.setRetryDelayMs(((Number) judge0.get("retry-delay-ms")).intValue());

            return judge0Config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    @Test
    @Order(1)
    @DisplayName("서버 연결 상태 확인")
    void checkServerStatus() {
        boolean connected = judge0Client.checkServerStatus();

        System.out.println("=".repeat(50));
        System.out.println("Judge0 서버 상태: " + (connected ? "연결됨" : "연결 실패"));
        System.out.println("API URL: " + judge0Config.getApiUrl());
        System.out.println("=".repeat(50));

        Assumptions.assumeTrue(connected, "Judge0 서버에 연결할 수 없습니다. 서버 상태를 확인하세요.");
    }

    @Test
    @Order(2)
    @DisplayName("Python Hello World 테스트")
    void testPythonHelloWorld() {
        assumeServerConnected();

        String sourceCode = "print(\"Hello, World!\")";
        String language = "python";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("Python Hello World", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
        assertThat(result.isAccepted()).isTrue();
        assertThat(result.getStdout()).isEqualTo("Hello, World!\n");
    }

    @Test
    @Order(3)
    @DisplayName("Python 두 수의 합 테스트")
    void testPythonSum() {
        assumeServerConnected();

        String sourceCode = """
                a, b = map(int, input().split())
                print(a + b)
                """;
        String language = "python";
        String stdin = "3 5";
        String expectedOutput = "8\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("Python 두 수의 합", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
        assertThat(result.getStdout()).isEqualTo("8\n");
    }

    @Test
    @Order(4)
    @DisplayName("Java Hello World 테스트")
    void testJavaHelloWorld() {
        assumeServerConnected();

        String sourceCode = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;
        String language = "java";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("Java Hello World", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    @Test
    @Order(5)
    @DisplayName("C Hello World 테스트")
    void testCHelloWorld() {
        assumeServerConnected();

        String sourceCode = """
                #include <stdio.h>

                int main() {
                    printf("Hello, World!\\n");
                    return 0;
                }
                """;
        String language = "c";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("C Hello World", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    @Test
    @Order(6)
    @DisplayName("C++ Hello World 테스트")
    void testCppHelloWorld() {
        assumeServerConnected();

        String sourceCode = """
                #include <iostream>
                using namespace std;

                int main() {
                    cout << "Hello, World!" << endl;
                    return 0;
                }
                """;
        String language = "cpp";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("C++ Hello World", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    @Test
    @Order(7)
    @DisplayName("JavaScript Hello World 테스트")
    void testJavaScriptHelloWorld() {
        assumeServerConnected();

        String sourceCode = "console.log(\"Hello, World!\");";
        String language = "javascript";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("JavaScript Hello World", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    @Test
    @Order(8)
    @DisplayName("오답(Wrong Answer) 테스트")
    void testWrongAnswer() {
        assumeServerConnected();

        String sourceCode = "print(\"Wrong Answer\")";
        String language = "python";
        String stdin = "";
        String expectedOutput = "Hello, World!\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("오답 테스트", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.WRONG_ANSWER);
        assertThat(result.isAccepted()).isFalse();
    }

    @Test
    @Order(9)
    @DisplayName("런타임 에러 테스트")
    void testRuntimeError() {
        assumeServerConnected();

        String sourceCode = "x = 1 / 0";
        String language = "python";
        String stdin = "";
        String expectedOutput = "";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("런타임 에러 테스트", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.RUNTIME_ERROR);
    }

    @Test
    @Order(10)
    @DisplayName("컴파일 에러 테스트 (Java)")
    void testCompileError() {
        assumeServerConnected();

        String sourceCode = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Missing semicolon")
                    }
                }
                """;
        String language = "java";
        String stdin = "";
        String expectedOutput = "";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("컴파일 에러 테스트", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.COMPILE_ERROR);
        assertThat(result.getCompileOutput()).isNotEmpty();
    }

    @Test
    @Order(11)
    @DisplayName("지원하지 않는 언어 테스트")
    void testUnsupportedLanguage() {
        String sourceCode = "print('test')";
        String language = "unsupported_language";
        String stdin = "";
        String expectedOutput = "";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("지원하지 않는 언어 테스트", result);

        assertThat(result.getErrorMessage()).contains("지원하지 않는 언어");
    }

    @Test
    @Order(12)
    @DisplayName("복잡한 알고리즘 테스트 - 피보나치")
    void testFibonacci() {
        assumeServerConnected();

        String sourceCode = """
                def fib(n):
                    if n <= 1:
                        return n
                    a, b = 0, 1
                    for _ in range(2, n + 1):
                        a, b = b, a + b
                    return b

                n = int(input())
                print(fib(n))
                """;
        String language = "python";
        String stdin = "10";
        String expectedOutput = "55\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("피보나치 테스트", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
        assertThat(result.getStdout()).isEqualTo("55\n");
    }

    @Test
    @Order(13)
    @DisplayName("여러 줄 입출력 테스트")
    void testMultiLineIO() {
        assumeServerConnected();

        String sourceCode = """
                n = int(input())
                for i in range(1, n + 1):
                    print(i)
                """;
        String language = "python";
        String stdin = "5";
        String expectedOutput = "1\n2\n3\n4\n5\n";

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        printResult("여러 줄 입출력 테스트", result);

        assertThat(result.getResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    private void assumeServerConnected() {
        Assumptions.assumeTrue(
                judge0Client.checkServerStatus(),
                "Judge0 서버에 연결할 수 없습니다."
        );
    }

    private void printResult(String testName, Judge0Client.JudgeResult result) {
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("테스트: " + testName);
        System.out.println("=".repeat(50));
        System.out.println("결과: " + result.getResult());
        System.out.println("상태: " + result.getStatusDescription());
        System.out.println("stdout (raw): [" + result.getStdout() + "]");
        System.out.println("stdout (escaped): " + (result.getStdout() != null ? result.getStdout().replace("\n", "\\n") : "null"));
        System.out.println("stderr: " + (result.getStderr() != null ? result.getStderr() : "null"));
        System.out.println("compileOutput: " + (result.getCompileOutput() != null ? result.getCompileOutput() : "null"));
        System.out.println("message: " + (result.getMessage() != null ? result.getMessage() : "null"));
        System.out.println("executionTime: " + result.getExecutionTime());
        System.out.println("memoryUsage: " + result.getMemoryUsage());
        System.out.println("errorMessage: " + result.getErrorMessage());
        System.out.println("=".repeat(50));
    }
}
