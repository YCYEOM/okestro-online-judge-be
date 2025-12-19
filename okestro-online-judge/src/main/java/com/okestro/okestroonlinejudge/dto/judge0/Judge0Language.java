package com.okestro.okestroonlinejudge.dto.judge0;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Judge0 지원 언어 ID 매핑.
 */
@Getter
@RequiredArgsConstructor
public enum Judge0Language {

    C("c", 50, "C (GCC 9.2.0)"),
    CPP("cpp", 54, "C++ (GCC 9.2.0)"),
    CPP17("cpp17", 76, "C++ (GCC 9.2.0, C++17)"),
    JAVA("java", 62, "Java (OpenJDK 13.0.1)"),
    PYTHON("python", 71, "Python (3.8.1)"),
    PYTHON2("python2", 70, "Python (2.7.17)"),
    JAVASCRIPT("javascript", 63, "JavaScript (Node.js 12.14.0)"),
    GO("go", 60, "Go (1.13.5)"),
    RUST("rust", 73, "Rust (1.40.0)"),
    KOTLIN("kotlin", 78, "Kotlin (1.3.70)"),
    SWIFT("swift", 83, "Swift (5.2.3)"),
    RUBY("ruby", 72, "Ruby (2.7.0)"),
    CSHARP("csharp", 51, "C# (Mono 6.6.0.161)"),
    TYPESCRIPT("typescript", 74, "TypeScript (3.7.4)");

    private final String languageName;
    private final int languageId;
    private final String description;

    public static Integer getLanguageId(String languageName) {
        return Arrays.stream(values())
                .filter(lang -> lang.languageName.equalsIgnoreCase(languageName))
                .findFirst()
                .map(Judge0Language::getLanguageId)
                .orElse(null);
    }

    public static boolean isSupported(String languageName) {
        return Arrays.stream(values())
                .anyMatch(lang -> lang.languageName.equalsIgnoreCase(languageName));
    }
}
