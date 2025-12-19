package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.UserStatisticsEntity;
import com.okestro.okestroonlinejudge.dto.response.OrganizationRankingResponse;
import com.okestro.okestroonlinejudge.dto.response.UserRankingResponse;
import com.okestro.okestroonlinejudge.repository.UserStatisticsRepository;
import com.okestro.okestroonlinejudge.repository.projection.OrganizationRankingProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 랭킹 관련 로직을 처리하는 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final UserStatisticsRepository userStatisticsRepository;

    /**
     * 사용자 랭킹 조회.
     *
     * @param pageable 페이징 정보
     * @return 랭킹 정보 페이지
     */
    public Page<UserRankingResponse> getUserRankings(Pageable pageable) {
        log.info("사용자 랭킹 조회 시작 - 페이지: {}, 사이즈: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<UserStatisticsEntity> statsPage = userStatisticsRepository.findUserRankings(pageable);
        
        log.info("조회된 통계 데이터 수: {}, 전체 개수: {}", 
                statsPage.getContent().size(), statsPage.getTotalElements());
        
        List<UserRankingResponse> content = new ArrayList<>();
        long startRank = pageable.getOffset() + 1;
        int index = 0;
        
        for (UserStatisticsEntity stats : statsPage.getContent()) {
            try {
                String tierName = stats.getUserEntity().getTierEntity() != null 
                        ? stats.getUserEntity().getTierEntity().getGroupName() 
                        : "Unranked";
                
                UserRankingResponse response = UserRankingResponse.builder()
                        .rank((int)(startRank + index))
                        .userId(stats.getUserEntity().getId())
                        .username(stats.getUserEntity().getUsername())
                        .tierName(tierName)
                        .solvedCount(stats.getSolvedCount())
                        .rankingPoint(stats.getRankingPoint())
                        .organizationName(stats.getUserEntity().getOrganizationName())
                        .profileImage(stats.getUserEntity().getProfileImage())
                        .build();
                
                content.add(response);
                
                log.debug("랭킹 데이터 추가 - 순위: {}, 사용자: {}, 포인트: {}", 
                        response.getRank(), response.getUsername(), response.getRankingPoint());
                
            } catch (Exception e) {
                log.error("랭킹 데이터 생성 중 오류 발생 - userId: {}, error: {}", 
                        stats.getUserEntity().getId(), e.getMessage(), e);
            }
            index++;
        }
        
        log.info("랭킹 조회 완료 - 반환 데이터 수: {}", content.size());
        
        return new PageImpl<>(content, pageable, statsPage.getTotalElements());
    }

    /**
     * 조직 랭킹 조회.
     *
     * @param pageable 페이징 정보
     * @return 조직 랭킹 정보 페이지
     */
    public Page<OrganizationRankingResponse> getOrganizationRankings(Pageable pageable) {
        Page<OrganizationRankingProjection> projPage = userStatisticsRepository.findOrganizationRankings(pageable);
        
        List<OrganizationRankingResponse> content = new ArrayList<>();
        long startRank = pageable.getOffset() + 1;
        int index = 0;

        for (OrganizationRankingProjection proj : projPage.getContent()) {
            content.add(OrganizationRankingResponse.builder()
                    .rank((int)(startRank + index))
                    .organizationId(proj.getOrganizationId())
                    .organizationName(proj.getOrganizationName())
                    .totalSolvedCount(proj.getTotalSolvedCount())
                    .totalRankingPoint(proj.getTotalRankingPoint())
                    .userCount(proj.getUserCount())
                    .build());
            index++;
        }
        
        return new PageImpl<>(content, pageable, projPage.getTotalElements());
    }
}



