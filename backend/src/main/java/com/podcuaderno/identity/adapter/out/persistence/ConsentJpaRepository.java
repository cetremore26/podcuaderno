package com.podcuaderno.identity.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ConsentJpaRepository extends JpaRepository<ConsentJpaEntity, UUID> {

    List<ConsentJpaEntity> findByUserIdOrderByAcceptedAtAsc(UUID userId);
}
