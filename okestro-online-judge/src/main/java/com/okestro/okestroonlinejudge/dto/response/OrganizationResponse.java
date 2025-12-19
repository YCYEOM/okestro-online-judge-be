package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.OrganizationEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 조직 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class OrganizationResponse {

    /**
     * 조직 ID
     */
    private Long id;

    /**
     * 상위 조직 ID
     */
    private Long parentId;

    /**
     * 상위 조직 이름
     */
    private String parentName;

    /**
     * 조직 이름
     */
    private String name;

    /**
     * 조직 계층 깊이
     */
    private Integer depth;

    /**
     * 하위 조직 수
     */
    private Integer childrenCount;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    /**
     * Entity에서 Response DTO로 변환.
     *
     * @param entity 조직 엔티티
     * @return OrganizationResponse
     */
    public static OrganizationResponse from(OrganizationEntity entity) {
        return OrganizationResponse.builder()
                .id(entity.getId())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .parentName(entity.getParent() != null ? entity.getParent().getName() : null)
                .name(entity.getName())
                .depth(entity.getDepth())
                .childrenCount(entity.getChildren() != null ? entity.getChildren().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
