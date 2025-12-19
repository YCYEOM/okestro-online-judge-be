package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 문제 검색 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Setter
@Schema(description = "문제 검색 및 필터링 요청")
public class SearchProblemRequest {

    @Schema(description = "페이지 번호 (0부터 시작)", defaultValue = "0")
    private int page = 0;

    @Schema(description = "페이지 크기", defaultValue = "10")
    private int size = 10;

    @Schema(description = "검색어 (제목 또는 내용)", example = "알고리즘")
    private String keyword;

    @Schema(description = "난이도 ID 목록 (티어 ID)", example = "[1, 2, 3]")
    private List<Integer> tierIds;

    @Schema(description = "태그 ID 목록", example = "[5, 10]")
    private List<Long> tagIds;

    @Schema(description = "문제 상태 (PUBLISHED, DRAFT 등)", example = "PUBLISHED")
    private String status;

    @Schema(description = "정렬 기준 (id, title, tierId, createdAt)", defaultValue = "id")
    private String sort = "id";

    @Schema(description = "정렬 방향 (ASC, DESC)", defaultValue = "DESC")
    private String direction = "DESC";

    /**
     * 정렬 방향 변환.
     */
    public Sort.Direction getSortDirection() {
        if ("ASC".equalsIgnoreCase(direction)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }
}

