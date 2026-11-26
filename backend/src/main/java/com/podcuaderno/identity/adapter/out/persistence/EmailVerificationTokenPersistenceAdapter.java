package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.application.port.out.EmailVerificationTokenRepository;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class EmailVerificationTokenPersistenceAdapter implements EmailVerificationTokenRepository {

    private final EmailVerificationTokenJpaRepository jpa;

    EmailVerificationTokenPersistenceAdapter(EmailVerificationTokenJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(EmailVerificationToken token) {
        jpa.save(EmailVerificationTokenMapper.toEntity(token));
    }

    @Override
    public Optional<EmailVerificationToken> findByHash(TokenHash hash) {
        return jpa.findByTokenHash(hash.value()).map(EmailVerificationTokenMapper::toDomain);
    }

    @Override
    public Optional<Instant> findLatestIssuedAt(UserId userId) {
        return jpa.findFirstByUserIdOrderByCreatedAtDesc(userId.value())
                .map(EmailVerificationTokenJpaEntity::getCreatedAt);
    }
}
