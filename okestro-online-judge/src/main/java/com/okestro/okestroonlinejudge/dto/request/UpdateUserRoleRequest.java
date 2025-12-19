package com.okestro.okestroonlinejudge.dto.request;

import com.okestro.okestroonlinejudge.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 권한 변경 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class UpdateUserRoleRequest {

    @NotNull(message = "권한은 필수입니다")
    private Role role;
}
