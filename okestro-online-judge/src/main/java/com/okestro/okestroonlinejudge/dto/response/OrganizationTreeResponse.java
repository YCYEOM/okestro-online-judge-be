package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.OrganizationEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 조직 트리 구조 응답 DTO.
 * 계층적 조직 구조를 표현하기 위한 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class OrganizationTreeResponse {

    /**
     * 조직 ID
     */
    private Long id;

    /**
     * 조직 이름
     */
    private String name;

    /**
     * 조직 계층 깊이
     */
    private Integer depth;

    /**
     * 하위 조직 목록
     */
    private List<OrganizationTreeResponse> children;

    /**
     * Entity에서 트리 구조 Response DTO로 변환.
     *
     * @param entity 조직 엔티티
     * @return OrganizationTreeResponse
     */
    public static OrganizationTreeResponse from(OrganizationEntity entity) {
        List<OrganizationTreeResponse> childrenList = new ArrayList<>();
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            childrenList = entity.getChildren().stream()
                    .map(OrganizationTreeResponse::from)
                    .collect(Collectors.toList());
        }

        return OrganizationTreeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .depth(entity.getDepth())
                .children(childrenList)
                .build();
    }
}
