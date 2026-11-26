package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.identity.domain.model.ConsentId;
import com.podcuaderno.shared.domain.UserId;

final class ConsentMapper {

    private ConsentMapper() {}

    static ConsentJpaEntity toEntity(Consent consent) {
        return new ConsentJpaEntity(
                consent.id().value(), consent.userId().value(), consent.policyVersion(), consent.acceptedAt());
    }

    static Consent toDomain(ConsentJpaEntity entity) {
        return new Consent(
                new ConsentId(entity.getId()),
                new UserId(entity.getUserId()),
                entity.getPolicyVersion(),
                entity.getAcceptedAt());
    }
}
