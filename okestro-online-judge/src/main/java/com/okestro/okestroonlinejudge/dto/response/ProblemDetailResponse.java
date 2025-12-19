package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 상세 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetailResponse {

    private Long id;
    private String title;
    private String content; // Markdown content
    private String inputDesc;
    private String outputDesc;
    private String tierGroup;
    private Integer tierLevel;
    private Integer difficulty;
    private Integer timeLimitMs;
    private Integer memoryLimitKb;

    // 통계 정보
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;

    // 생성자 정보
    private CreatorInfo creator;

    /**
     * 문제 생성자 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatorInfo {
        private Long id;
        private String userName;
        private String profileImage;
        private String tier;
        private Integer tierLevel;
        private String title;
        private String badge;
        private String profileBorder;
        private String nameColor;
        private String cardBackground;
    }

    public static ProblemDetailResponse from(ProblemEntity problemEntity, String content,
            String profileImage, String profileBorder, String badge, String title, String nameColor, String cardBackground) {
        CreatorInfo creatorInfo = null;
        if (problemEntity.getCreator() != null) {
            creatorInfo = CreatorInfo.builder()
                    .id(problemEntity.getCreator().getId())
                    .userName(problemEntity.getCreator().getUsername())
                    .profileImage(profileImage)  // 파라미터로 받은 장착된 아바타 사용
                    .tier(problemEntity.getCreator().getTierEntity() != null
                            ? problemEntity.getCreator().getTierEntity().getGroupName() : null)
                    .tierLevel(problemEntity.getCreator().getTierEntity() != null
                            ? problemEntity.getCreator().getTierEntity().getLevel() : null)
                    .title(title)
                    .badge(badge)
                    .profileBorder(profileBorder)
                    .nameColor(nameColor)
                    .cardBackground(cardBackground)
                    .build();
        }

        return ProblemDetailResponse.builder()
                .id(problemEntity.getId())
                .title(problemEntity.getTitle())
                .content(content)
                .inputDesc(problemEntity.getInputDesc())
                .outputDesc(problemEntity.getOutputDesc())
                .tierGroup(problemEntity.getTierEntity().getGroupName())
                .tierLevel(problemEntity.getTierEntity().getLevel())
                .timeLimitMs(problemEntity.getTimeLimitMs())
                .memoryLimitKb(problemEntity.getMemoryLimitKb())
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .isLiked(false)
                .creator(creatorInfo)
                .build();
    }

    public static ProblemDetailResponse from(ProblemEntity problemEntity, String content) {
        return from(problemEntity, content, null, null, null, null, null, null);
    }

    public static ProblemDetailResponse from(ProblemEntity problemEntity, String content,
            Long viewCount, Long likeCount, Long commentCount, Boolean isLiked,
            String profileImage, String profileBorder, String badge, String title, String nameColor, String cardBackground) {
        CreatorInfo creatorInfo = null;
        if (problemEntity.getCreator() != null) {
            creatorInfo = CreatorInfo.builder()
                    .id(problemEntity.getCreator().getId())
                    .userName(problemEntity.getCreator().getUsername())
                    .profileImage(profileImage)  // 파라미터로 받은 장착된 아바타 사용
                    .tier(problemEntity.getCreator().getTierEntity() != null
                            ? problemEntity.getCreator().getTierEntity().getGroupName() : null)
                    .tierLevel(problemEntity.getCreator().getTierEntity() != null
                            ? problemEntity.getCreator().getTierEntity().getLevel() : null)
                    .title(title)
                    .badge(badge)
                    .profileBorder(profileBorder)
                    .nameColor(nameColor)
                    .cardBackground(cardBackground)
                    .build();
        }

        return ProblemDetailResponse.builder()
                .id(problemEntity.getId())
                .title(problemEntity.getTitle())
                .content(content)
                .inputDesc(problemEntity.getInputDesc())
                .outputDesc(problemEntity.getOutputDesc())
                .tierGroup(problemEntity.getTierEntity().getGroupName())
                .tierLevel(problemEntity.getTierEntity().getLevel())
                .timeLimitMs(problemEntity.getTimeLimitMs())
                .memoryLimitKb(problemEntity.getMemoryLimitKb())
                .viewCount(viewCount)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .isLiked(isLiked)
                .creator(creatorInfo)
                .build();
    }

    public static ProblemDetailResponse from(ProblemEntity problemEntity, String content,
            Long viewCount, Long likeCount, Long commentCount, Boolean isLiked) {
        return from(problemEntity, content, viewCount, likeCount, commentCount, isLiked, null, null, null, null, null, null);
    }
}


