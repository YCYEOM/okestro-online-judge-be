package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 조직 랭킹 응답 DTO.
 */
@Getter
@Builder
public class OrganizationRankingResponse {

    private Integer rank;
    private Long organizationId;
    private String organizationName;
    private Long totalSolvedCount;
    private Long totalRankingPoint;
    private Long userCount;
}



