package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.request.CommentRequest;
import com.okestro.okestroonlinejudge.dto.response.CommentResponse;
import com.okestro.okestroonlinejudge.repository.CommentLikeRepository;
import com.okestro.okestroonlinejudge.repository.CommentRepository;
import com.okestro.okestroonlinejudge.repository.SolutionRepository;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성.
     */
    @Transactional
    public CommentResponse create(Long solutionId, CommentRequest request, String username) {
        SolutionEntity solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다."));
        UserEntity user = getUserByUsername(username);

        CommentEntity comment = CommentEntity.builder()
                .solution(solution)
                .user(user)
                .content(request.getContent())
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    /**
     * 댓글 목록 조회.
     */
    public Page<CommentResponse> getComments(Long solutionId, Pageable pageable, String username) {
        SolutionEntity solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다."));

        // TODO: 댓글 조회 권한 체크 (솔루션 볼 수 있는 사람만 댓글도 볼 수 있음)
        // 현재는 별도 체크 없음. (솔루션 상세 조회 시 체크하므로)

        Page<CommentEntity> comments = commentRepository.findBySolution(solution, pageable);
        
        return comments.map(comment -> {
            CommentResponse response = CommentResponse.from(comment);
            if (username != null) {
                response.setIsAuthor(comment.getUser().getUsername().equals(username));
            }
            return response;
        });
    }

    /**
     * 댓글 수정.
     */
    @Transactional
    public CommentResponse update(Long commentId, CommentRequest request, String username) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        UserEntity user = getUserByUsername(username);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.update(request.getContent());
        return CommentResponse.from(comment);
    }

    /**
     * 댓글 삭제.
     */
    @Transactional
    public void delete(Long commentId, String username) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        UserEntity user = getUserByUsername(username);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    /**
     * 댓글 좋아요 토글.
     */
    @Transactional
    public void toggleLike(Long commentId, String username) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        UserEntity user = getUserByUsername(username);

        commentLikeRepository.findByCommentAndUser(comment, user)
                .ifPresentOrElse(
                        commentLikeRepository::delete,
                        () -> commentLikeRepository.save(CommentLikeEntity.builder()
                                .comment(comment)
                                .user(user)
                                .build())
                );
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
