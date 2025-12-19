package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.SubmissionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시도한 문제 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptedProblemResponse {

    /**
     * 문제 ID
     */
    private Long id;

    /**
     * 문제 제목
     */
    private String title;

    /**
     * 난이도 (Easy, Medium, Hard)
     */
    private String difficulty;

    /**
     * 결과 상태 (Accepted, Failed)
     */
    private String status;

    /**
     * 시도 일시
     */
    private LocalDateTime attemptedAt;

    /**
     * 사용 언어
     */
    private String language;

    /**
     * 시도 횟수
     */
    private int tryCount;

    /**
     * Entity로부터 DTO 생성.
     */
    public static AttemptedProblemResponse from(SubmissionEntity submission, int tryCount) {
        String difficulty = "Medium"; // 기본값
        if (submission.getProblemEntity().getTierEntity() != null) {
            String groupName = submission.getProblemEntity().getTierEntity().getGroupName();
            if (groupName != null) {
                if (groupName.equalsIgnoreCase("BRONZE") || groupName.equalsIgnoreCase("SILVER")) {
                    difficulty = "Easy";
                } else if (groupName.equalsIgnoreCase("GOLD") || groupName.equalsIgnoreCase("PLATINUM")) {
                    difficulty = "Medium";
                } else {
                    difficulty = "Hard";
                }
            }
        }

        String status = submission.getResult() == SubmissionResult.ACCEPTED ? "Accepted" : "Failed";

        return AttemptedProblemResponse.builder()
                .id(submission.getProblemEntity().getId())
                .title(submission.getProblemEntity().getTitle())
                .difficulty(difficulty)
                .status(status)
                .attemptedAt(submission.getCreatedAt())
                .language(submission.getLanguage())
                .tryCount(tryCount)
                .build();
    }
}
