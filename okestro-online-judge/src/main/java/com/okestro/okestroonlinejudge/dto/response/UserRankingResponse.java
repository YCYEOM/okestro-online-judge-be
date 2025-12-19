package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 랭킹 응답 DTO.
 */
@Getter
@Builder
public class UserRankingResponse {

    private Integer rank;
    private Long userId;
    private String username;
    private String tierName;
    private Long solvedCount;
    private Long rankingPoint;
    private String organizationName;
    private String profileImage;
}



