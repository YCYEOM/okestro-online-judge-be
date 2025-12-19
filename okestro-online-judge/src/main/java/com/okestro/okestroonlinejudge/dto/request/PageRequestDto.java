package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 페이징 요청 DTO.
 */
@Getter
@Setter
@Schema(description = "페이징 요청")
public class PageRequestDto {

    @Schema(description = "페이지 번호 (0부터 시작)", defaultValue = "0")
    private int page = 0;

    @Schema(description = "페이지 크기", defaultValue = "20")
    private int size = 20;

    /**
     * Pageable 객체로 변환.
     */
    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}



