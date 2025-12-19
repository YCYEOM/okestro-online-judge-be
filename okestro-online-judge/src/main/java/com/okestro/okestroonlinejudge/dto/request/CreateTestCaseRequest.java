package com.okestro.okestroonlinejudge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트케이스 생성 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestCaseRequest {

    /**
     * 입력 데이터
     */
    private String input;

    /**
     * 기대 출력 데이터
     */
    private String expectedOutput;

    /**
     * 예제 여부
     */
    private Boolean isSample;
}
