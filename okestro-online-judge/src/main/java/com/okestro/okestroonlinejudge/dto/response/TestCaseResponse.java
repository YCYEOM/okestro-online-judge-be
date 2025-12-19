package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트케이스 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponse {

    /**
     * 테스트케이스 ID
     */
    private Long id;

    /**
     * 문제 ID
     */
    private Long problemId;

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

    /**
     * 엔티티에서 응답 DTO 생성.
     *
     * @param entity 테스트케이스 엔티티
     * @param input 입력 데이터
     * @param expectedOutput 기대 출력 데이터
     * @return 응답 DTO
     */
    public static TestCaseResponse from(TestCaseEntity entity, String input, String expectedOutput) {
        return TestCaseResponse.builder()
                .id(entity.getId())
                .problemId(entity.getProblemEntity().getId())
                .input(input)
                .expectedOutput(expectedOutput)
                .isSample(entity.getIsSample())
                .build();
    }
}
