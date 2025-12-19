package com.okestro.okestroonlinejudge.dto.judge0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Judge0Language 단위 테스트")
class Judge0LanguageTest {

    @ParameterizedTest
    @DisplayName("언어 이름으로 언어 ID 조회")
    @CsvSource({
            "python, 71",
            "java, 62",
            "c, 50",
            "cpp, 54",
            "javascript, 63",
            "go, 60",
            "rust, 73",
            "kotlin, 78"
    })
    void getLanguageId(String languageName, int expectedId) {
        Integer languageId = Judge0Language.getLanguageId(languageName);

        assertThat(languageId).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("대소문자 구분 없이 언어 ID 조회")
    void getLanguageIdCaseInsensitive() {
        assertThat(Judge0Language.getLanguageId("PYTHON")).isEqualTo(71);
        assertThat(Judge0Language.getLanguageId("Python")).isEqualTo(71);
        assertThat(Judge0Language.getLanguageId("JAVA")).isEqualTo(62);
    }

    @Test
    @DisplayName("존재하지 않는 언어 ID 조회시 null 반환")
    void getLanguageIdNotFound() {
        Integer languageId = Judge0Language.getLanguageId("unknown_language");

        assertThat(languageId).isNull();
    }

    @ParameterizedTest
    @DisplayName("지원 언어 확인")
    @ValueSource(strings = {"python", "java", "c", "cpp", "javascript", "go", "rust"})
    void isSupported(String languageName) {
        assertThat(Judge0Language.isSupported(languageName)).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 언어 확인")
    void isNotSupported() {
        assertThat(Judge0Language.isSupported("unknown")).isFalse();
        assertThat(Judge0Language.isSupported("")).isFalse();
    }

    @Test
    @DisplayName("모든 언어 enum 값 확인")
    void allLanguagesHaveValidIds() {
        for (Judge0Language lang : Judge0Language.values()) {
            assertThat(lang.getLanguageId()).isPositive();
            assertThat(lang.getLanguageName()).isNotEmpty();
            assertThat(lang.getDescription()).isNotEmpty();
        }
    }
}
