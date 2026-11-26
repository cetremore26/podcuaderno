package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.domain.model.RefreshToken;
import org.springframework.stereotype.Component;

@Component
class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpa;

    RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(RefreshToken token) {
        jpa.save(RefreshTokenMapper.toEntity(token));
    }
}
