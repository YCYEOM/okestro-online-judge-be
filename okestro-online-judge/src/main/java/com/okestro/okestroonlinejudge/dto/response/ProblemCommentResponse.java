package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.okestro.okestroonlinejudge.domain.ProblemCommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 문제 댓글 응답 DTO.
 */
@Schema(description = "문제 댓글 응답")
@Getter
@Builder
public class ProblemCommentResponse {

    @Schema(description = "댓글 ID")
    private Long id;

    @Schema(description = "작성자 ID")
    private Long userId;

    @Schema(description = "작성자명")
    private String username;

    @Schema(description = "작성자 닉네임")
    private String nickname;

    @Schema(description = "작성자 티어")
    private String userTier;

    @Schema(description = "작성자 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "삭제 여부")
    private boolean isDeleted;

    @Schema(description = "본인 작성 여부")
    private boolean isAuthor;

    @Schema(description = "대댓글 목록")
    private List<ProblemCommentResponse> replies;

    @Schema(description = "생성 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Entity -> DTO 변환
     */
    public static ProblemCommentResponse from(ProblemCommentEntity comment, Long currentUserId, List<ProblemCommentResponse> replies) {
        boolean isAuthor = currentUserId != null && comment.getUser().getId().equals(currentUserId);

        return ProblemCommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .nickname(comment.getUser().getUsername()) // username을 nickname으로 사용
                .userTier(comment.getUser().getTierEntity() != null
                        ? comment.getUser().getTierEntity().getGroupName()
                        : "Bronze")
                .profileImageUrl(comment.getUser().getProfileImage()) // profileImage 사용
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .isAuthor(isAuthor)
                .replies(replies)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
