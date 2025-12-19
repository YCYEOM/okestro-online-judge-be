package com.okestro.okestroonlinejudge;

import com.okestro.okestroonlinejudge.config.Judge0Config;
import com.okestro.okestroonlinejudge.client.Judge0Client;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import org.springframework.web.client.RestTemplate;

/**
 * Judge0 API 테스트 러너.
 * Gradle 테스트 대신 직접 실행할 수 있는 메인 클래스입니다.
 *
 * 실행 방법:
 * 1. IDE에서 직접 실행
 * 2. 또는 빌드 후: java -cp build/libs/*.jar com.okestro.okestroonlinejudge.Judge0TestRunner
 *
 * 환경 변수:
 * - JUDGE0_API_URL: Judge0 API 엔드포인트 (기본값: http://localhost:2358)
 */
public class Judge0TestRunner {

    private final Judge0Client judge0Client;
    private int passed = 0;
    private int failed = 0;

    public Judge0TestRunner(String apiUrl) {
        Judge0Config config = new Judge0Config();
        config.setApiUrl(apiUrl);
        config.setCpuTimeLimit(2.0);
        config.setMemoryLimit(128000);
        config.setMaxRetries(20);
        config.setRetryDelayMs(1000);

        RestTemplate restTemplate = new RestTemplate();
        this.judge0Client = new Judge0Client(restTemplate, config);
    }

    public static void main(String[] args) {
        String apiUrl = System.getenv("JUDGE0_API_URL");
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = "http://localhost:2358";
        }

        System.out.println("=".repeat(60));
        System.out.println("Judge0 API 테스트 러너");
        System.out.println("API URL: " + apiUrl);
        System.out.println("=".repeat(60));

        Judge0TestRunner runner = new Judge0TestRunner(apiUrl);
        runner.runAllTests();
    }

    public void runAllTests() {
        // 서버 연결 확인
        if (!testServerConnection()) {
            System.out.println("\n[ERROR] Judge0 서버에 연결할 수 없습니다.");
            System.out.println("서버 상태를 확인하거나 JUDGE0_API_URL 환경변수를 설정하세요.");
            return;
        }

        // 테스트 실행
        testPythonHelloWorld();
        testPythonSum();
        testJavaHelloWorld();
        testCHelloWorld();
        testCppHelloWorld();
        testJavaScriptHelloWorld();
        testWrongAnswer();
        testRuntimeError();
        testCompileError();
        testFibonacci();

        // 결과 요약
        printSummary();
    }

    private boolean testServerConnection() {
        System.out.println("\n[TEST] 서버 연결 확인...");
        boolean connected = judge0Client.checkServerStatus();
        System.out.println(connected ? "[OK] 서버 연결 성공" : "[FAIL] 서버 연결 실패");
        return connected;
    }

    private void testPythonHelloWorld() {
        runTest("Python Hello World",
                "print(\"Hello, World!\")",
                "python", "", "Hello, World!\n",
                SubmissionResult.ACCEPTED);
    }

    private void testPythonSum() {
        runTest("Python 두 수의 합",
                "a, b = map(int, input().split())\nprint(a + b)",
                "python", "3 5", "8\n",
                SubmissionResult.ACCEPTED);
    }

    private void testJavaHelloWorld() {
        String code = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;
        runTest("Java Hello World", code, "java", "", "Hello, World!\n",
                SubmissionResult.ACCEPTED);
    }

    private void testCHelloWorld() {
        String code = """
                #include <stdio.h>

                int main() {
                    printf("Hello, World!\\n");
                    return 0;
                }
                """;
        runTest("C Hello World", code, "c", "", "Hello, World!\n",
                SubmissionResult.ACCEPTED);
    }

    private void testCppHelloWorld() {
        String code = """
                #include <iostream>
                using namespace std;

                int main() {
                    cout << "Hello, World!" << endl;
                    return 0;
                }
                """;
        runTest("C++ Hello World", code, "cpp", "", "Hello, World!\n",
                SubmissionResult.ACCEPTED);
    }

    private void testJavaScriptHelloWorld() {
        runTest("JavaScript Hello World",
                "console.log(\"Hello, World!\");",
                "javascript", "", "Hello, World!\n",
                SubmissionResult.ACCEPTED);
    }

    private void testWrongAnswer() {
        runTest("오답 테스트 (WRONG_ANSWER 예상)",
                "print(\"Wrong Answer\")",
                "python", "", "Hello, World!\n",
                SubmissionResult.WRONG_ANSWER);
    }

    private void testRuntimeError() {
        runTest("런타임 에러 테스트 (RUNTIME_ERROR 예상)",
                "x = 1 / 0",
                "python", "", "",
                SubmissionResult.RUNTIME_ERROR);
    }

    private void testCompileError() {
        String code = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Missing semicolon")
                    }
                }
                """;
        runTest("컴파일 에러 테스트 (COMPILE_ERROR 예상)",
                code, "java", "", "",
                SubmissionResult.COMPILE_ERROR);
    }

    private void testFibonacci() {
        String code = """
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
        runTest("피보나치 알고리즘", code, "python", "10", "55\n",
                SubmissionResult.ACCEPTED);
    }

    private void runTest(String testName, String sourceCode, String language,
                         String stdin, String expectedOutput, SubmissionResult expectedResult) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("[TEST] " + testName);
        System.out.println("언어: " + language);

        Judge0Client.JudgeResult result = judge0Client.judge(sourceCode, language, stdin, expectedOutput);

        boolean success = result.getResult() == expectedResult;
        if (success) {
            passed++;
            System.out.println("[PASS] 결과: " + result.getResult());
        } else {
            failed++;
            System.out.println("[FAIL] 예상: " + expectedResult + ", 실제: " + result.getResult());
        }

        // 상세 정보 출력
        if (result.getStdout() != null && !result.getStdout().isEmpty()) {
            System.out.println("출력: " + result.getStdout().replace("\n", "\\n"));
        }
        if (result.getStderr() != null && !result.getStderr().isEmpty()) {
            System.out.println("에러: " + result.getStderr());
        }
        if (result.getCompileOutput() != null && !result.getCompileOutput().isEmpty()) {
            System.out.println("컴파일 출력: " + result.getCompileOutput().substring(0, Math.min(200, result.getCompileOutput().length())));
        }
        if (result.getExecutionTime() != null) {
            System.out.println("실행 시간: " + result.getExecutionTime() + "초");
        }
        if (result.getMemoryUsage() != null) {
            System.out.println("메모리: " + result.getMemoryUsage() + " KB");
        }
    }

    private void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("테스트 요약");
        System.out.println("=".repeat(60));
        System.out.println("총 테스트: " + (passed + failed));
        System.out.println("성공: " + passed);
        System.out.println("실패: " + failed);
        System.out.println("성공률: " + (passed * 100 / (passed + failed)) + "%");
        System.out.println("=".repeat(60));

        if (failed == 0) {
            System.out.println("\n모든 테스트 통과!");
        } else {
            System.out.println("\n일부 테스트 실패.");
        }
    }
}
