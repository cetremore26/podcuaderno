package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.EmailVerificationTokenId;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.shared.domain.UserId;

final class EmailVerificationTokenMapper {

    private EmailVerificationTokenMapper() {}

    static EmailVerificationTokenJpaEntity toEntity(EmailVerificationToken token) {
        return new EmailVerificationTokenJpaEntity(
                token.id().value(),
                token.userId().value(),
                token.tokenHash().value(),
                token.createdAt(),
                token.expiresAt(),
                token.usedAt().orElse(null));
    }

    static EmailVerificationToken toDomain(EmailVerificationTokenJpaEntity entity) {
        return EmailVerificationToken.restore(
                new EmailVerificationTokenId(entity.getId()),
                new UserId(entity.getUserId()),
                new TokenHash(entity.getTokenHash()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getUsedAt());
    }
}
