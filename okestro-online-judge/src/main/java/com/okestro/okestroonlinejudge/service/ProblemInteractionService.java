package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.request.ProblemCommentRequest;
import com.okestro.okestroonlinejudge.dto.response.ProblemCommentResponse;
import com.okestro.okestroonlinejudge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문제 상호작용 서비스 (좋아요, 댓글, 조회수).
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemInteractionService {

    private final ProblemRepository problemRepository;
    private final ProblemStatisticsRepository problemStatisticsRepository;
    private final ProblemLikeRepository problemLikeRepository;
    private final ProblemCommentRepository problemCommentRepository;
    private final UserRepository userRepository;

    // ==================== 조회수 ====================

    /**
     * 문제 조회수를 증가시킨다.
     *
     * @param problemId 문제 ID
     */
    @Transactional
    public void incrementViewCount(Long problemId) {
        ProblemStatisticsEntity stats = getOrCreateStatistics(problemId);
        stats.incrementViewCount();
    }

    /**
     * 문제 통계 정보를 조회한다.
     *
     * @param problemId 문제 ID
     * @return 문제 통계 정보
     */
    public ProblemStatisticsEntity getStatistics(Long problemId) {
        return problemStatisticsRepository.findById(problemId)
                .orElseGet(() -> {
                    // 통계가 없으면 새로 생성하여 반환 (저장하지 않음)
                    ProblemEntity problem = problemRepository.findById(problemId)
                            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId));
                    return new ProblemStatisticsEntity(problem);
                });
    }

    // ==================== 좋아요 ====================

    /**
     * 문제에 좋아요를 토글한다.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @return 좋아요 여부 (true: 좋아요 추가됨, false: 좋아요 취소됨)
     */
    @Transactional
    public boolean toggleLike(Long problemId, Long userId) {
        ProblemEntity problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        return problemLikeRepository.findByProblemIdAndUserId(problemId, userId)
                .map(like -> {
                    // 좋아요 취소
                    problemLikeRepository.delete(like);
                    ProblemStatisticsEntity stats = getOrCreateStatistics(problemId);
                    stats.decrementLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    // 좋아요 추가
                    ProblemLikeEntity newLike = ProblemLikeEntity.builder()
                            .problem(problem)
                            .user(user)
                            .build();
                    problemLikeRepository.save(newLike);
                    ProblemStatisticsEntity stats = getOrCreateStatistics(problemId);
                    stats.incrementLikeCount();
                    return true;
                });
    }

    /**
     * 사용자가 문제에 좋아요를 눌렀는지 확인한다.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @return 좋아요 여부
     */
    public boolean isLiked(Long problemId, Long userId) {
        if (userId == null) {
            return false;
        }
        return problemLikeRepository.existsByProblemIdAndUserId(problemId, userId);
    }

    // ==================== 댓글 ====================

    /**
     * 문제 댓글을 작성한다.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @param request 댓글 요청
     * @return 생성된 댓글 응답
     */
    @Transactional
    public ProblemCommentResponse createComment(Long problemId, Long userId, ProblemCommentRequest request) {
        ProblemEntity problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        ProblemCommentEntity parent = null;
        if (request.getParentId() != null) {
            parent = problemCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다. id=" + request.getParentId()));
        }

        ProblemCommentEntity comment = ProblemCommentEntity.builder()
                .problem(problem)
                .user(user)
                .parent(parent)
                .content(request.getContent())
                .build();

        ProblemCommentEntity saved = problemCommentRepository.save(comment);

        // 통계 업데이트
        ProblemStatisticsEntity stats = getOrCreateStatistics(problemId);
        stats.incrementCommentCount();

        return ProblemCommentResponse.from(saved, userId, Collections.emptyList());
    }

    /**
     * 문제 댓글을 수정한다.
     *
     * @param commentId 댓글 ID
     * @param userId 사용자 ID
     * @param request 댓글 요청
     * @return 수정된 댓글 응답
     */
    @Transactional
    public ProblemCommentResponse updateComment(Long commentId, Long userId, ProblemCommentRequest request) {
        ProblemCommentEntity comment = problemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. id=" + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(request.getContent());

        return ProblemCommentResponse.from(comment, userId, Collections.emptyList());
    }

    /**
     * 문제 댓글을 삭제한다 (소프트 삭제).
     *
     * @param commentId 댓글 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        ProblemCommentEntity comment = problemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. id=" + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        comment.delete();

        // 통계 업데이트
        ProblemStatisticsEntity stats = getOrCreateStatistics(comment.getProblem().getId());
        stats.decrementCommentCount();
    }

    /**
     * 문제 댓글 목록을 페이징하여 조회한다.
     *
     * @param problemId 문제 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param currentUserId 현재 사용자 ID (로그인 안한 경우 null)
     * @return 댓글 목록 (Page)
     */
    public Page<ProblemCommentResponse> getComments(Long problemId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProblemCommentEntity> comments = problemCommentRepository.findByProblemIdAndParentIsNull(problemId, pageable);

        return comments.map(comment -> {
            // 대댓글 조회
            List<ProblemCommentResponse> replies = problemCommentRepository.findByParentId(comment.getId())
                    .stream()
                    .map(reply -> ProblemCommentResponse.from(reply, currentUserId, Collections.emptyList()))
                    .collect(Collectors.toList());

            return ProblemCommentResponse.from(comment, currentUserId, replies);
        });
    }

    // ==================== Helper ====================

    /**
     * 문제 통계 정보를 조회하거나 없으면 생성한다.
     */
    @Transactional
    private ProblemStatisticsEntity getOrCreateStatistics(Long problemId) {
        return problemStatisticsRepository.findById(problemId)
                .orElseGet(() -> {
                    ProblemEntity problem = problemRepository.findById(problemId)
                            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + problemId));
                    ProblemStatisticsEntity newStats = new ProblemStatisticsEntity(problem);
                    return problemStatisticsRepository.save(newStats);
                });
    }
}
