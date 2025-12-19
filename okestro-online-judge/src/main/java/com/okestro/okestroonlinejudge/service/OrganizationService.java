package com.okestro.okestroonlinejudge.service;

import com.okestro.okestroonlinejudge.domain.OrganizationEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import com.okestro.okestroonlinejudge.dto.request.CreateOrganizationRequest;
import com.okestro.okestroonlinejudge.dto.request.UpdateOrganizationRequest;
import com.okestro.okestroonlinejudge.dto.response.OrganizationResponse;
import com.okestro.okestroonlinejudge.dto.response.OrganizationTreeResponse;
import com.okestro.okestroonlinejudge.dto.response.UserResponse;
import com.okestro.okestroonlinejudge.repository.OrganizationRepository;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 조직 관리 서비스.
 *
 * @author Assistant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    /**
     * 조직 생성.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 조직 정보
     */
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        if (organizationRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 조직 이름입니다: " + request.getName());
        }

        OrganizationEntity parent = null;
        int depth = 0;

        if (request.getParentId() != null) {
            parent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 조직을 찾을 수 없습니다: " + request.getParentId()));
            depth = parent.getDepth() + 1;
        }

        OrganizationEntity organization = OrganizationEntity.builder()
                .parent(parent)
                .name(request.getName())
                .depth(depth)
                .build();

        OrganizationEntity saved = organizationRepository.save(organization);
        return OrganizationResponse.from(saved);
    }

    /**
     * 조직 상세 조회.
     *
     * @param id 조직 ID
     * @return 조직 정보
     */
    public OrganizationResponse getOrganization(Long id) {
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조직을 찾을 수 없습니다: " + id));
        return OrganizationResponse.from(organization);
    }

    /**
     * 전체 조직 목록 조회.
     *
     * @return 조직 목록
     */
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 최상위 조직 목록 조회.
     *
     * @return 최상위 조직 목록
     */
    public List<OrganizationResponse> getRootOrganizations() {
        return organizationRepository.findByParentIsNullOrderByNameAsc().stream()
                .map(OrganizationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 조직의 하위 조직 목록 조회.
     *
     * @param parentId 상위 조직 ID
     * @return 하위 조직 목록
     */
    public List<OrganizationResponse> getChildOrganizations(Long parentId) {
        return organizationRepository.findByParentIdOrderByNameAsc(parentId).stream()
                .map(OrganizationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 조직 트리 구조 조회 (최상위 조직부터 계층적으로).
     *
     * @return 조직 트리 목록
     */
    public List<OrganizationTreeResponse> getOrganizationTree() {
        List<OrganizationEntity> rootOrganizations = organizationRepository.findByParentIsNullOrderByNameAsc();
        return rootOrganizations.stream()
                .map(OrganizationTreeResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 조직 수정.
     *
     * @param id      조직 ID
     * @param request 수정 요청 DTO
     * @return 수정된 조직 정보
     */
    @Transactional
    public OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request) {
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조직을 찾을 수 없습니다: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!organization.getName().equals(request.getName()) &&
                    organizationRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("이미 존재하는 조직 이름입니다: " + request.getName());
            }
            organization.updateName(request.getName());
        }

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("자기 자신을 상위 조직으로 설정할 수 없습니다");
            }
            OrganizationEntity newParent = organizationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 조직을 찾을 수 없습니다: " + request.getParentId()));

            if (isDescendantById(id, request.getParentId())) {
                throw new IllegalArgumentException("하위 조직을 상위 조직으로 설정할 수 없습니다");
            }
            organization.updateParent(newParent);
            updateChildrenDepthRecursively(id, organization.getDepth());
        }

        return OrganizationResponse.from(organization);
    }

    /**
     * 조직을 최상위로 이동.
     *
     * @param id 조직 ID
     * @return 수정된 조직 정보
     */
    @Transactional
    public OrganizationResponse moveToRoot(Long id) {
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조직을 찾을 수 없습니다: " + id));

        organization.updateParent(null);
        updateChildrenDepthRecursively(id, 0);

        return OrganizationResponse.from(organization);
    }

    /**
     * 조직 삭제.
     *
     * @param id 조직 ID
     */
    @Transactional
    public void deleteOrganization(Long id) {
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조직을 찾을 수 없습니다: " + id));

        if (organization.hasChildren()) {
            throw new IllegalArgumentException("하위 조직이 있는 조직은 삭제할 수 없습니다. 하위 조직을 먼저 삭제하거나 이동해주세요.");
        }

        organizationRepository.delete(organization);
    }

    /**
     * 특정 depth의 조직 목록 조회.
     *
     * @param depth 조직 깊이
     * @return 해당 depth의 조직 목록
     */
    public List<OrganizationResponse> getOrganizationsByDepth(Integer depth) {
        return organizationRepository.findByDepthOrderByNameAsc(depth).stream()
                .map(OrganizationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 하위 조직인지 확인 (순환 참조 방지) - ID 기반 조회.
     */
    private boolean isDescendantById(Long ancestorId, Long targetId) {
        OrganizationEntity target = organizationRepository.findById(targetId).orElse(null);
        while (target != null && target.getParent() != null) {
            if (target.getParent().getId().equals(ancestorId)) {
                return true;
            }
            target = target.getParent();
        }
        return false;
    }

    /**
     * 하위 조직들의 depth를 재귀적으로 업데이트 (Repository 조회 방식).
     */
    private void updateChildrenDepthRecursively(Long parentId, int parentDepth) {
        List<OrganizationEntity> children = organizationRepository.findByParentIdOrderByNameAsc(parentId);
        for (OrganizationEntity child : children) {
            int newDepth = parentDepth + 1;
            child.updateDepth(newDepth);
            updateChildrenDepthRecursively(child.getId(), newDepth);
        }
    }

    /**
     * 사용자를 조직에 할당.
     *
     * @param userId         사용자 ID
     * @param organizationId 조직 ID
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public UserResponse assignUserToOrganization(Long userId, Long organizationId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("조직을 찾을 수 없습니다: " + organizationId));

        user.updateOrganization(organization);
        return UserResponse.from(user);
    }

    /**
     * 사용자의 조직 할당 해제.
     *
     * @param userId 사용자 ID
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public UserResponse removeUserFromOrganization(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.updateOrganization(null);
        return UserResponse.from(user);
    }

    /**
     * 특정 조직에 속한 사용자 목록 조회.
     *
     * @param organizationId 조직 ID
     * @return 사용자 목록
     */
    public List<UserResponse> getUsersByOrganization(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("조직을 찾을 수 없습니다: " + organizationId);
        }
        return userRepository.findByOrganizationEntityId(organizationId).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 조직에 속한 사용자 수 조회.
     *
     * @param organizationId 조직 ID
     * @return 사용자 수
     */
    public long getUserCountByOrganization(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("조직을 찾을 수 없습니다: " + organizationId);
        }
        return userRepository.countByOrganizationEntityId(organizationId);
    }
}
