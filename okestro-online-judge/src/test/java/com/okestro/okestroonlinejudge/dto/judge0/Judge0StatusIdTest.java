package com.okestro.okestroonlinejudge.dto.judge0;

import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Judge0StatusId 단위 테스트")
class Judge0StatusIdTest {

    @ParameterizedTest
    @DisplayName("상태 ID를 SubmissionResult로 변환")
    @CsvSource({
            "1, WAITING",
            "2, JUDGING",
            "3, ACCEPTED",
            "4, WRONG_ANSWER",
            "5, TIME_LIMIT_EXCEEDED",
            "6, COMPILE_ERROR",
            "7, RUNTIME_ERROR",
            "8, RUNTIME_ERROR",
            "9, RUNTIME_ERROR",
            "10, RUNTIME_ERROR",
            "11, RUNTIME_ERROR",
            "12, RUNTIME_ERROR",
            "13, RUNTIME_ERROR",
            "14, RUNTIME_ERROR"
    })
    void toSubmissionResult(int statusId, SubmissionResult expectedResult) {
        SubmissionResult result = Judge0StatusId.toSubmissionResult(statusId);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("알 수 없는 상태 ID는 RUNTIME_ERROR 반환")
    void unknownStatusIdReturnsRuntimeError() {
        assertThat(Judge0StatusId.toSubmissionResult(999)).isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.toSubmissionResult(0)).isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.toSubmissionResult(-1)).isEqualTo(SubmissionResult.RUNTIME_ERROR);
    }

    @ParameterizedTest
    @DisplayName("완료된 상태 확인 (statusId >= 3)")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14})
    void isCompletedTrue(int statusId) {
        assertThat(Judge0StatusId.isCompleted(statusId)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("진행 중인 상태 확인 (statusId < 3)")
    @ValueSource(ints = {1, 2})
    void isCompletedFalse(int statusId) {
        assertThat(Judge0StatusId.isCompleted(statusId)).isFalse();
    }

    @Test
    @DisplayName("모든 enum 값이 유효한 상태 ID와 설명을 가짐")
    void allStatusIdsAreValid() {
        for (Judge0StatusId status : Judge0StatusId.values()) {
            assertThat(status.getStatusId()).isPositive();
            assertThat(status.getSubmissionResult()).isNotNull();
            assertThat(status.getDescription()).isNotBlank();
        }
    }

    @Test
    @DisplayName("ACCEPTED 상태 ID는 3")
    void acceptedStatusIdIs3() {
        assertThat(Judge0StatusId.ACCEPTED.getStatusId()).isEqualTo(3);
        assertThat(Judge0StatusId.ACCEPTED.getSubmissionResult()).isEqualTo(SubmissionResult.ACCEPTED);
    }

    @Test
    @DisplayName("런타임 에러 상태들이 모두 RUNTIME_ERROR로 매핑")
    void runtimeErrorStatuses() {
        assertThat(Judge0StatusId.RUNTIME_ERROR_SIGSEGV.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.RUNTIME_ERROR_SIGXFSZ.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.RUNTIME_ERROR_SIGFPE.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.RUNTIME_ERROR_SIGABRT.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.RUNTIME_ERROR_NZEC.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
        assertThat(Judge0StatusId.RUNTIME_ERROR_OTHER.getSubmissionResult())
                .isEqualTo(SubmissionResult.RUNTIME_ERROR);
    }

    @Nested
    @DisplayName("한글 설명 (getDescription) 테스트")
    class GetDescriptionTest {

        @ParameterizedTest
        @DisplayName("상태 ID별 한글 설명 확인")
        @CsvSource({
                "1, 대기열에서 대기 중",
                "2, 채점 진행 중",
                "3, 정답",
                "4, 오답",
                "5, 시간 제한 초과",
                "6, 컴파일 에러"
        })
        void getDescriptionForKnownStatusId(int statusId, String expectedDescription) {
            assertThat(Judge0StatusId.getDescription(statusId)).isEqualTo(expectedDescription);
        }

        @Test
        @DisplayName("알 수 없는 상태 ID는 '알 수 없는 상태' 반환")
        void getDescriptionForUnknownStatusId() {
            assertThat(Judge0StatusId.getDescription(999)).isEqualTo("알 수 없는 상태");
            assertThat(Judge0StatusId.getDescription(0)).isEqualTo("알 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("정답 여부 (isAccepted) 테스트")
    class IsAcceptedTest {

        @Test
        @DisplayName("상태 ID 3은 정답")
        void statusId3IsAccepted() {
            assertThat(Judge0StatusId.isAccepted(3)).isTrue();
        }

        @ParameterizedTest
        @DisplayName("상태 ID 3이 아니면 오답")
        @ValueSource(ints = {1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 999})
        void otherStatusIdsAreNotAccepted(int statusId) {
            assertThat(Judge0StatusId.isAccepted(statusId)).isFalse();
        }
    }

    @Nested
    @DisplayName("FE 타입 호환성 테스트")
    class FrontendCompatibilityTest {

        @Test
        @DisplayName("FE profile.d.ts의 SubmissionHistory.status 타입과 일치")
        void submissionResultDisplayNameMatchesFrontendType() {
            // FE profile.d.ts:
            // status: 'Accepted' | 'Wrong Answer' | 'Time Limit' | 'Runtime Error' | 'Compile Error'

            assertThat(SubmissionResult.ACCEPTED.toDisplayName()).isEqualTo("Accepted");
            assertThat(SubmissionResult.WRONG_ANSWER.toDisplayName()).isEqualTo("Wrong Answer");
            assertThat(SubmissionResult.TIME_LIMIT_EXCEEDED.toDisplayName()).isEqualTo("Time Limit");
            assertThat(SubmissionResult.RUNTIME_ERROR.toDisplayName()).isEqualTo("Runtime Error");
            assertThat(SubmissionResult.COMPILE_ERROR.toDisplayName()).isEqualTo("Compile Error");
        }

        @Test
        @DisplayName("진행 중 상태도 displayName을 가짐")
        void inProgressStatusesHaveDisplayName() {
            assertThat(SubmissionResult.WAITING.toDisplayName()).isEqualTo("Waiting");
            assertThat(SubmissionResult.JUDGING.toDisplayName()).isEqualTo("Judging");
        }

        @Test
        @DisplayName("Memory Limit도 displayName을 가짐")
        void memoryLimitHasDisplayName() {
            assertThat(SubmissionResult.MEMORY_LIMIT_EXCEEDED.toDisplayName()).isEqualTo("Memory Limit");
        }
    }

    @Nested
    @DisplayName("SubmissionResult isFinal/isAccepted 테스트")
    class SubmissionResultMethodsTest {

        @Test
        @DisplayName("진행 중 상태는 최종 상태가 아님")
        void inProgressStatesAreNotFinal() {
            assertThat(SubmissionResult.WAITING.isFinal()).isFalse();
            assertThat(SubmissionResult.JUDGING.isFinal()).isFalse();
        }

        @Test
        @DisplayName("완료 상태는 최종 상태")
        void completedStatesAreFinal() {
            assertThat(SubmissionResult.ACCEPTED.isFinal()).isTrue();
            assertThat(SubmissionResult.WRONG_ANSWER.isFinal()).isTrue();
            assertThat(SubmissionResult.TIME_LIMIT_EXCEEDED.isFinal()).isTrue();
            assertThat(SubmissionResult.MEMORY_LIMIT_EXCEEDED.isFinal()).isTrue();
            assertThat(SubmissionResult.RUNTIME_ERROR.isFinal()).isTrue();
            assertThat(SubmissionResult.COMPILE_ERROR.isFinal()).isTrue();
        }

        @Test
        @DisplayName("ACCEPTED만 isAccepted() == true")
        void onlyAcceptedIsAccepted() {
            assertThat(SubmissionResult.ACCEPTED.isAccepted()).isTrue();

            assertThat(SubmissionResult.WAITING.isAccepted()).isFalse();
            assertThat(SubmissionResult.JUDGING.isAccepted()).isFalse();
            assertThat(SubmissionResult.WRONG_ANSWER.isAccepted()).isFalse();
            assertThat(SubmissionResult.TIME_LIMIT_EXCEEDED.isAccepted()).isFalse();
            assertThat(SubmissionResult.MEMORY_LIMIT_EXCEEDED.isAccepted()).isFalse();
            assertThat(SubmissionResult.RUNTIME_ERROR.isAccepted()).isFalse();
            assertThat(SubmissionResult.COMPILE_ERROR.isAccepted()).isFalse();
        }
    }
}
