package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.application.port.out.ConsentRepository;
import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class ConsentPersistenceAdapter implements ConsentRepository {

    private final ConsentJpaRepository jpa;

    ConsentPersistenceAdapter(ConsentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Consent consent) {
        jpa.save(ConsentMapper.toEntity(consent));
    }

    @Override
    public List<Consent> findByUserId(UserId userId) {
        return jpa.findByUserIdOrderByAcceptedAtAsc(userId.value()).stream()
                .map(ConsentMapper::toDomain)
                .toList();
    }
}
