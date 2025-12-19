package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.*;
import com.okestro.okestroonlinejudge.dto.request.CreateSolutionRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateSolutionRequest;
import com.okestro.okestroonlinejudge.dto.response.SolutionDetailResponse;
import com.okestro.okestroonlinejudge.dto.response.SolutionResponse;
import com.okestro.okestroonlinejudge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final SolutionLikeRepository solutionLikeRepository;
    private final CommentRepository commentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final UserProblemStatusRepository userProblemStatusRepository;

    /**
     * 정답 공유 생성.
     *
     * @param request  요청 DTO
     * @param username 작성자 username
     * @return 생성된 솔루션 상세 정보
     */
    @Transactional
    public SolutionDetailResponse create(CreateSolutionRequest request, String username) {
        UserEntity user = getUserByUsername(username);
        SubmissionEntity submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출입니다. ID: " + request.getSubmissionId()));

        // 작성자 검증
        if (!submission.getUserEntity().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 제출만 공유할 수 있습니다.");
        }

        // 정답 여부 검증 (FE 요구사항에는 없지만 정책상 추가)
        if (submission.getResult() != SubmissionResult.ACCEPTED) {
            throw new IllegalArgumentException("정답 처리된 제출만 공유할 수 있습니다.");
        }

        // 중복 공유 검증
        if (solutionRepository.existsBySubmissionId(submission.getId())) {
            throw new IllegalArgumentException("이미 공유된 제출입니다.");
        }

        SolutionEntity solution = SolutionEntity.builder()
                .user(user)
                .problem(submission.getProblemEntity())
                .submission(submission)
                .language(submission.getLanguage())
                .code(submission.getSourceCode()) // 제출 당시 코드 사용
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .build();

        SolutionEntity savedSolution = solutionRepository.save(solution);

        return SolutionDetailResponse.of(savedSolution, 0, 0, false);
    }

    /**
     * 정답 공유 상세 조회.
     *
     * @param solutionId 솔루션 ID
     * @param username   조회 요청자 username (nullable)
     * @return 솔루션 상세 정보
     */
    public SolutionDetailResponse getSolution(Long solutionId, String username) {
        SolutionEntity solution = solutionRepository.findByIdWithUserAndProblem(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다. ID: " + solutionId));

        long likeCount = solutionLikeRepository.countBySolution(solution);
        long commentCount = commentRepository.countBySolution(solution); // CommentRepository에 메서드 추가 필요
        boolean liked = false;

        // 로그인한 경우 권한 및 좋아요 체크
        if (username != null) {
            UserEntity user = getUserByUsername(username);
            liked = solutionLikeRepository.existsBySolutionAndUser(solution, user);

            // 작성자가 아닌 경우 권한 체크
            if (!solution.getUser().getId().equals(user.getId())) {
                // 1. 공개 여부 체크
                if (!solution.isVisibility()) {
                    throw new IllegalArgumentException("비공개 솔루션입니다.");
                }
                // 2. 문제 풀이 여부 체크 (해당 문제를 맞춘 사용자만 열람 가능)
                boolean solved = submissionRepository.existsByProblemEntity_IdAndUserEntity_IdAndResult(
                        solution.getProblem().getId(), user.getId(), SubmissionResult.ACCEPTED);

                if (!solved) {
                    throw new IllegalArgumentException("해당 문제를 먼저 해결해야 정답 코드를 볼 수 있습니다.");
                }
            }
        } else {
            // 비로그인 사용자는 접근 불가 (혹은 공개된 것만 접근 가능하되 코드는 가림 처리? -> 정책상 로그인 필수)
             throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        return SolutionDetailResponse.of(solution, likeCount, commentCount, liked);
    }

    /**
     * 정답 공유 목록 조회.
     *
     * @param problemId 문제 ID
     * @param language  언어 필터 (Optional)
     * @param pageable  페이징 정보
     * @param username  요청자 username (nullable)
     * @return 솔루션 목록
     */
    public Page<SolutionResponse> getSolutions(Long problemId, String language, Pageable pageable, String username) {
        ProblemEntity problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제. ID: " + problemId));

        // 목록 조회 시에는 본인이 푼 문제인지 체크하지 않음 (목록은 보여주되 클릭 시 차단, 혹은 목록 자체도 차단? -> 보통 목록은 보여줌)
        // 다만, "비공개" 솔루션은 작성자 본인에게만 보여야 함.
        // 현재 요구사항 단순화를 위해 공개된 솔루션만 조회하도록 함.
        // 만약 작성자가 요청하면 본인의 비공개 솔루션도 포함해야 한다면 쿼리가 복잡해짐.
        // 일단 공개된 솔루션만 반환.

        Page<SolutionEntity> solutions;
        if (language != null && !language.isEmpty()) {
            solutions = solutionRepository.findByProblemAndLanguageAndVisibilityTrue(problem, language, pageable);
        } else {
            solutions = solutionRepository.findByProblemAndVisibilityTrue(problem, pageable);
        }
        
        return solutions.map(SolutionResponse::from);
    }

    /**
     * 정답 공유 수정.
     */
    @Transactional
    public void update(Long solutionId, UpdateSolutionRequest request, String username) {
        SolutionEntity solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다."));
        UserEntity user = getUserByUsername(username);

        if (!solution.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        solution.update(request.getDescription(), request.getVisibility());
    }

    /**
     * 정답 공유 삭제.
     */
    @Transactional
    public void delete(Long solutionId, String username) {
        SolutionEntity solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다."));
        UserEntity user = getUserByUsername(username);

        if (!solution.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        solutionRepository.delete(solution);
    }

    /**
     * 좋아요 토글.
     */
    @Transactional
    public void toggleLike(Long solutionId, String username) {
        SolutionEntity solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 솔루션입니다."));
        UserEntity user = getUserByUsername(username);

        solutionLikeRepository.findBySolutionAndUser(solution, user)
                .ifPresentOrElse(
                        solutionLikeRepository::delete,
                        () -> solutionLikeRepository.save(SolutionLikeEntity.builder()
                                .solution(solution)
                                .user(user)
                                .build())
                );
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
