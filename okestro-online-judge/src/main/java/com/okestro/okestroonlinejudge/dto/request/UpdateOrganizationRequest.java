package com.okestro.okestroonlinejudge.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 조직 수정 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class UpdateOrganizationRequest {

    /**
     * 새로운 상위 조직 ID (null이면 최상위 조직으로 변경)
     */
    private Long parentId;

    /**
     * 새로운 조직 이름
     */
    @Size(max = 100, message = "조직 이름은 100자를 초과할 수 없습니다")
    private String name;
}
