package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.RefreshTokenFamilyId;
import com.podcuaderno.identity.domain.model.TokenHash;
import java.time.Instant;
import java.util.Optional;
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

    @Override
    public Optional<RefreshToken> findByHash(TokenHash hash) {
        return jpa.findByTokenHash(hash.value()).map(RefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByHashForUpdate(TokenHash hash) {
        return jpa.findWithLockByTokenHash(hash.value()).map(RefreshTokenMapper::toDomain);
    }

    @Override
    public void revokeFamily(RefreshTokenFamilyId familyId, Instant now) {
        jpa.revokeFamily(familyId.value(), now);
    }
}
