package com.okestro.okestroonlinejudge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 조직 생성 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class CreateOrganizationRequest {

    /**
     * 상위 조직 ID (null이면 최상위 조직)
     */
    private Long parentId;

    /**
     * 조직 이름
     */
    @NotBlank(message = "조직 이름은 필수입니다")
    @Size(max = 100, message = "조직 이름은 100자를 초과할 수 없습니다")
    private String name;
}
