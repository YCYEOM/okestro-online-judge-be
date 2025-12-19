package com.okestro.okestroonlinejudge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 제출 결과 상태를 나타내는 열거형.
 *
 * FE 타입 정의와 일치:
 * status: 'Accepted' | 'Wrong Answer' | 'Time Limit' | 'Runtime Error' | 'Compile Error'
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SubmissionResult {
    WAITING("Waiting", false),
    JUDGING("Judging", false),
    ACCEPTED("Accepted", true),
    WRONG_ANSWER("Wrong Answer", true),
    TIME_LIMIT_EXCEEDED("Time Limit", true),
    MEMORY_LIMIT_EXCEEDED("Memory Limit", true),
    RUNTIME_ERROR("Runtime Error", true),
    COMPILE_ERROR("Compile Error", true);

    /**
     * FE에서 표시되는 상태 문자열.
     * FE profile.d.ts의 SubmissionHistory.status 타입과 일치해야 함.
     */
    private final String displayName;

    /**
     * 채점이 완료된 최종 상태인지 여부.
     */
    private final boolean finalStatus;

    /**
     * FE로 전달할 상태 문자열 반환.
     *
     * @return FE 타입과 일치하는 상태 문자열
     */
    public String toDisplayName() {
        return displayName;
    }

    /**
     * 채점 완료 여부 확인.
     *
     * @return 최종 상태면 true
     */
    public boolean isFinal() {
        return finalStatus;
    }

    /**
     * 정답 여부 확인.
     *
     * @return ACCEPTED면 true
     */
    public boolean isAccepted() {
        return this == ACCEPTED;
    }
}


