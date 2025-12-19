package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 태그 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem_tag")
public class ProblemTagEntity {

    /**
     * 태그 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 태그 이름 (고유값)
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * ProblemTag 생성자.
     *
     * @param name 태그 이름
     */
    public ProblemTagEntity(String name) {
        this.name = name;
    }
}


