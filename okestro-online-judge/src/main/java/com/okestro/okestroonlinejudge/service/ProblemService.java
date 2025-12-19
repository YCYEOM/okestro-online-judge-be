package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import com.okestro.okestroonlinejudge.domain.ShopItemType;
import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import com.okestro.okestroonlinejudge.domain.TierEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import com.okestro.okestroonlinejudge.domain.UserInventoryEntity;
import com.okestro.okestroonlinejudge.dto.request.CreateProblemRequest;
import com.okestro.okestroonlinejudge.dto.request.SearchProblemRequest;
import com.okestro.okestroonlinejudge.dto.response.ProblemDetailResponse;
import com.okestro.okestroonlinejudge.repository.ProblemRepository;
import com.okestro.okestroonlinejudge.repository.ProblemStatisticsRepository;
import com.okestro.okestroonlinejudge.repository.TestCaseRepository;
import com.okestro.okestroonlinejudge.repository.TierRepository;
import com.okestro.okestroonlinejudge.repository.UserInventoryRepository;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import com.okestro.okestroonlinejudge.repository.specification.ProblemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 문제 관리 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TierRepository tierRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ProblemStatisticsRepository problemStatisticsRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserInventoryRepository userInventoryRepository;

    @Value("${minio.bucket-name:okestro-problems}")
    private String bucketName;

    /**
     * 문제 목록 조회 (검색 및 페이징).
     */
    public Page<ProblemDetailResponse> getProblems(SearchProblemRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSortDirection(), request.getSort())
        );

        Specification<ProblemEntity> spec = ProblemSpecification.search(request);
        Page<ProblemEntity> problems = problemRepository.findAll(spec, pageable);

        return problems.map(problem -> {
            // 내용(content)은 목록 조회 시에는 필요 없을 수 있으나, 기존 로직 유지를 위해 읽어옴.
            // 성능 이슈 발생 시 요약 정보만 반환하는 별도 DTO 사용 고려.
            // 여기서는 목록 조회 시에도 content를 읽지만, 실제로는 목록에서는 content가 필요 없을 수 있음.
            // ProblemSummaryResponse가 더 적합할 수 있음.
            String content = storageService.readString(problem.getContentPath(), bucketName);
            return ProblemDetailResponse.from(problem, content);
        });
    }

    /**
     * 문제 목록 조회 (기존 페이징 호환용).
     */
    public Page<ProblemDetailResponse> getProblems(Pageable pageable) {
        return problemRepository.findAll(pageable)
                .map(problem -> {
                    String content = storageService.readString(problem.getContentPath(), bucketName);
                    return ProblemDetailResponse.from(problem, content);
                });
    }

    /**
     * 문제 상세 조회
     */
    public ProblemDetailResponse getProblem(Long id) {
        ProblemEntity problem = problemRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));
        String content = storageService.readString(problem.getContentPath(), bucketName);

        // Creator의 장착 아이템 정보 가져오기
        String profileImage = null;
        String profileBorder = null;
        String badge = null;
        String title = null;
        String nameColor = null;
        String cardBackground = null;

        if (problem.getCreator() != null) {
            List<UserInventoryEntity> equippedItems = userInventoryRepository.findByUserIdAndIsEquippedTrue(problem.getCreator().getId());
            for (UserInventoryEntity item : equippedItems) {
                ShopItemType type = item.getShopItem().getType();
                String value = item.getShopItem().getItemValue();

                switch (type) {
                    case AVATAR -> profileImage = value; // 장착된 아바타 사용
                    case PROFILE_BORDER -> profileBorder = value;
                    case BADGE -> badge = item.getShopItem().getPreviewUrl(); // Badge는 previewUrl 사용
                    case TITLE -> title = value;
                    case NAME_COLOR -> nameColor = value;
                    case CARD_BACKGROUND -> cardBackground = value;
                }
            }

            // 장착된 아바타가 없으면 UserEntity의 profileImage 사용
            if (profileImage == null) {
                profileImage = problem.getCreator().getProfileImage();
            }
        }

        return ProblemDetailResponse.from(problem, content, profileImage, profileBorder, badge, title, nameColor, cardBackground);
    }

    /**
     * 문제 생성
     */
    @Transactional
    public ProblemDetailResponse createProblem(CreateProblemRequest request, String username) {
        // 티어 조회 (difficulty 값으로 티어 ID 매핑)
        TierEntity tier = tierRepository.findById(Long.valueOf(request.getDifficulty()))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 난이도입니다. difficulty=" + request.getDifficulty()));

        // 생성자 조회 (username은 실제로 JWT의 subject인 email)
        UserEntity creator = null;
        if (username != null) {
            // JWT subject가 email이므로 email로 검색
            creator = userRepository.findByEmail(username).orElse(null);
            if (creator == null) {
                System.err.println("WARNING: User not found for email: " + username);
            } else {
                System.out.println("INFO: Creator set to user: " + creator.getId() + " (" + creator.getUsername() + ")");
            }
        } else {
            System.err.println("WARNING: Username is null, creator will not be set");
        }

        // 마크다운 컨텐츠를 MinIO에 저장
        String fileName = "problem_" + UUID.randomUUID() + ".md";
        String contentPath = storageService.uploadString(request.getContent(), fileName, bucketName);

        // 문제 엔티티 생성
        ProblemEntity problem = ProblemEntity.builder()
                .title(request.getTitle())
                .contentPath(contentPath)
                .inputDesc(request.getInputDesc())
                .outputDesc(request.getOutputDesc())
                .tierEntity(tier)
                .creator(creator)
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 1000)
                .memoryLimitKb(request.getMemoryLimitKb() != null ? request.getMemoryLimitKb() : 128000)
                .build();

        ProblemEntity saved = problemRepository.save(problem);
        return ProblemDetailResponse.from(saved, request.getContent());
    }

    /**
     * 문제 수정
     */
    @Transactional
    public ProblemDetailResponse updateProblem(Long id, CreateProblemRequest request) {
        ProblemEntity problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));

        // 티어 조회
        TierEntity tier = tierRepository.findById(Long.valueOf(request.getDifficulty()))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 난이도입니다. difficulty=" + request.getDifficulty()));

        // 마크다운 컨텐츠 업데이트 (기존 파일명 재사용)
        storageService.uploadString(request.getContent(), problem.getContentPath(), bucketName);

        // 문제 정보 업데이트
        problem.update(
                request.getTitle(),
                request.getInputDesc(),
                request.getOutputDesc(),
                tier,
                request.getTimeLimitMs(),
                request.getMemoryLimitKb()
        );

        return ProblemDetailResponse.from(problem, request.getContent());
    }

    /**
     * 추천 문제 목록 조회 (홈 페이지용)
     * 최근 생성된 문제 또는 많이 풀린 문제를 반환합니다.
     */
    public List<ProblemDetailResponse> getFeaturedProblems(int limit) {
        // 최근 생성된 문제를 기준으로 조회
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProblemEntity> problems = problemRepository.findAll(pageable);

        return problems.stream()
                .map(problem -> {
                    // 홈 페이지용이므로 상세 내용은 불필요 (null 전달)
                    return ProblemDetailResponse.from(problem, null);
                })
                .toList();
    }

    /**
     * 문제 삭제
     */
    @Transactional
    public void deleteProblem(Long id) {
        ProblemEntity problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));

        // 관련된 테스트케이스 먼저 삭제
        List<TestCaseEntity> testCases = testCaseRepository.findByProblemEntity_Id(id);
        if (!testCases.isEmpty()) {
            testCaseRepository.deleteAll(testCases);
        }

        // 관련된 통계 데이터가 있으면 삭제
        problemStatisticsRepository.findById(id).ifPresent(statistics -> {
            problemStatisticsRepository.delete(statistics);
        });

        problemRepository.delete(problem);
    }
}


