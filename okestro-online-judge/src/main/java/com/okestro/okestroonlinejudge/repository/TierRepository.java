package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.TierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierRepository extends JpaRepository<TierEntity, Long> {
}

