package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 조직(학교, 회사 등) 정보를 나타내는 엔티티.
 * 계층 구조를 지원하며, depth 필드로 조직의 깊이를 관리한다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "organization", indexes = {
        @Index(name = "idx_organization_parent_id", columnList = "parent_id"),
        @Index(name = "idx_organization_depth", columnList = "depth")
})
public class OrganizationEntity extends BaseTimeEntity {

    /**
     * 조직 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 상위 조직 (계층 구조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrganizationEntity parent;

    /**
     * 하위 조직 목록
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<OrganizationEntity> children = new ArrayList<>();

    /**
     * 조직 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 조직 계층 깊이 (0: 최상위 조직)
     */
    @Column(nullable = false)
    private Integer depth;

    /**
     * Organization 생성자.
     *
     * @param parent 상위 조직
     * @param name 조직 이름
     * @param depth 계층 깊이
     */
    @Builder
    public OrganizationEntity(OrganizationEntity parent, String name, Integer depth) {
        this.parent = parent;
        this.name = name;
        this.depth = depth;
    }

    /**
     * 조직 정보를 수정한다.
     *
     * @param name 새로운 조직 이름
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 상위 조직을 변경하고 depth를 재계산한다.
     *
     * @param newParent 새로운 상위 조직
     */
    public void updateParent(OrganizationEntity newParent) {
        this.parent = newParent;
        this.depth = (newParent == null) ? 0 : newParent.getDepth() + 1;
    }

    /**
     * depth를 직접 업데이트한다.
     *
     * @param depth 새로운 depth 값
     */
    public void updateDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     * 최상위 조직인지 확인한다.
     *
     * @return 최상위 조직 여부
     */
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * 하위 조직이 있는지 확인한다.
     *
     * @return 하위 조직 존재 여부
     */
    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }
}

