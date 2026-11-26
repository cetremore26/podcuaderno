package com.podcuaderno.metrics.adapter.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface UsageEventJpaRepository extends JpaRepository<UsageEventJpaEntity, UUID> {}
