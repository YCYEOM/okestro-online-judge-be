package com.okestro.okestroonlinejudge.repository.projection;

public interface OrganizationRankingProjection {
    Long getOrganizationId();
    String getOrganizationName();
    Long getTotalSolvedCount();
    Long getTotalRankingPoint();
    Long getUserCount();
}



