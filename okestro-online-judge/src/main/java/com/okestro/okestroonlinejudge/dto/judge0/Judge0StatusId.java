package com.okestro.okestroonlinejudge.dto.judge0;

import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Judge0 상태 ID와 SubmissionResult 매핑.
 *
 * Judge0 Status ID 참조:
 * https://ce.judge0.com/statuses
 *
 * FE 타입 정의 (profile.d.ts):
 * status: 'Accepted' | 'Wrong Answer' | 'Time Limit' | 'Runtime Error' | 'Compile Error'
 */
@Getter
@RequiredArgsConstructor
public enum Judge0StatusId {

    // 대기 상태 (채점 미완료)
    IN_QUEUE(1, SubmissionResult.WAITING, "대기열에서 대기 중"),
    PROCESSING(2, SubmissionResult.JUDGING, "채점 진행 중"),

    // 성공 상태
    ACCEPTED(3, SubmissionResult.ACCEPTED, "정답"),

    // 오답 상태
    WRONG_ANSWER(4, SubmissionResult.WRONG_ANSWER, "오답"),

    // 시간/메모리 제한 초과
    TIME_LIMIT_EXCEEDED(5, SubmissionResult.TIME_LIMIT_EXCEEDED, "시간 제한 초과"),

    // 컴파일 에러
    COMPILATION_ERROR(6, SubmissionResult.COMPILE_ERROR, "컴파일 에러"),

    // 런타임 에러 (다양한 원인)
    RUNTIME_ERROR_SIGSEGV(7, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 세그멘테이션 폴트"),
    RUNTIME_ERROR_SIGXFSZ(8, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 파일 크기 제한 초과"),
    RUNTIME_ERROR_SIGFPE(9, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 부동 소수점 예외"),
    RUNTIME_ERROR_SIGABRT(10, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 비정상 종료"),
    RUNTIME_ERROR_NZEC(11, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 0이 아닌 종료 코드"),
    RUNTIME_ERROR_OTHER(12, SubmissionResult.RUNTIME_ERROR, "런타임 에러: 기타"),

    // 시스템 에러
    INTERNAL_ERROR(13, SubmissionResult.RUNTIME_ERROR, "내부 에러"),
    EXEC_FORMAT_ERROR(14, SubmissionResult.RUNTIME_ERROR, "실행 형식 에러");

    private final int statusId;
    private final SubmissionResult submissionResult;
    private final String description;

    /**
     * Judge0 상태 ID를 SubmissionResult로 변환.
     *
     * @param statusId Judge0 상태 ID
     * @return 대응하는 SubmissionResult
     */
    public static SubmissionResult toSubmissionResult(int statusId) {
        for (Judge0StatusId status : values()) {
            if (status.statusId == statusId) {
                return status.submissionResult;
            }
        }
        return SubmissionResult.RUNTIME_ERROR;
    }

    /**
     * Judge0 상태 ID에 대한 한글 설명 반환.
     *
     * @param statusId Judge0 상태 ID
     * @return 한글 설명
     */
    public static String getDescription(int statusId) {
        for (Judge0StatusId status : values()) {
            if (status.statusId == statusId) {
                return status.description;
            }
        }
        return "알 수 없는 상태";
    }

    /**
     * 채점 완료 여부 확인.
     * 상태 ID 3 이상이면 채점 완료 (대기/처리 중이 아님).
     *
     * @param statusId Judge0 상태 ID
     * @return 채점 완료면 true
     */
    public static boolean isCompleted(int statusId) {
        return statusId >= 3;
    }

    /**
     * Judge0 상태 ID가 성공(정답)인지 확인.
     *
     * @param statusId Judge0 상태 ID
     * @return 정답이면 true
     */
    public static boolean isAccepted(int statusId) {
        return statusId == ACCEPTED.statusId;
    }
}
