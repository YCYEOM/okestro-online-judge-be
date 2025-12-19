package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.okestro.okestroonlinejudge.domain.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO.
 */
@Schema(description = "댓글 응답")
@Getter
@Builder
public class CommentResponse {

    @Schema(description = "댓글 ID")
    private Long id;

    @Schema(description = "작성자명")
    private String username;

    @Schema(description = "작성자 티어")
    private String userTier;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "본인 작성 여부")
    private boolean isAuthor;

    @Schema(description = "생성 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static CommentResponse from(CommentEntity comment) {
        // TODO: isAuthor 처리를 위해서는 요청자 정보가 필요한데,
        // 정적 메서드라 파라미터 추가 필요. 일단 false로 두고 Service에서 처리하거나,
        // 여기서 username을 받도록 수정해야 함.
        // 간소화를 위해 isAuthor는 제외하거나 null 처리.
        // FE에서 로그인 유저 정보와 비교하여 처리 가능하므로 username 반환이 중요함.

        return CommentResponse.builder()
                .id(comment.getId())
                .username(comment.getUser().getUsername())
                .userTier(comment.getUser().getTierEntity().getGroupName())
                .content(comment.getContent())
                .isAuthor(false) // Service 레벨에서 설정 필요
                .createdAt(comment.getCreatedAt())
                .build();
    }
    
    // isAuthor 설정을 위한 추가 메서드
    public void setIsAuthor(boolean isAuthor) {
        this.isAuthor = isAuthor;
    }
}
