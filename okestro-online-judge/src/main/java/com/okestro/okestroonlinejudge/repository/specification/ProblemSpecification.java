package com.okestro.okestroonlinejudge.repository.specification;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import com.okestro.okestroonlinejudge.domain.ProblemTagEntity;
import com.okestro.okestroonlinejudge.dto.request.SearchProblemRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 문제 검색 조건을 정의하는 Specification.
 *
 * @author Assistant
 * @since 1.0
 */
public class ProblemSpecification {

    public static Specification<ProblemEntity> search(SearchProblemRequest request) {
        return (root, query, criteriaBuilder) -> {
            Specification<ProblemEntity> spec = Specification.where(null);

            // 검색어 (제목)
            if (StringUtils.hasText(request.getKeyword())) {
                spec = spec.and((r, q, cb) ->
                        cb.like(r.get("title"), "%" + request.getKeyword() + "%"));
            }

            // 난이도 필터
            if (request.getTierIds() != null && !request.getTierIds().isEmpty()) {
                spec = spec.and((r, q, cb) ->
                        r.get("tierEntity").get("level").in(request.getTierIds()));
            }

            // 태그 필터
            if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
                spec = spec.and((r, q, cb) -> {
                    // 중복 제거를 위해 distinct 적용
                    q.distinct(true);
                    Join<ProblemEntity, ProblemTagEntity> problemTags = r.join("problemTags", JoinType.INNER); // ProblemEntity에 problemTags 매핑 필요
                    return problemTags.get("tagEntity").get("id").in(request.getTagIds());
                });
            }

            // 상태 필터 (만약 ProblemEntity에 status 필드가 있다면)
            // if (StringUtils.hasText(request.getStatus())) {
            //     spec = spec.and((r, q, cb) ->
            //             cb.equal(r.get("status"), request.getStatus()));
            // }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}

