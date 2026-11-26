package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.RefreshTokenFamilyId;
import com.podcuaderno.identity.domain.model.RefreshTokenId;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.shared.domain.UserId;

final class RefreshTokenMapper {

    private RefreshTokenMapper() {}

    static RefreshTokenJpaEntity toEntity(RefreshToken token) {
        return new RefreshTokenJpaEntity(
                token.id().value(),
                token.userId().value(),
                token.familyId().value(),
                token.tokenHash().value(),
                token.createdAt(),
                token.expiresAt(),
                token.rotatedAt().orElse(null),
                token.revokedAt().orElse(null));
    }

    static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.restore(
                new RefreshTokenId(entity.getId()),
                new UserId(entity.getUserId()),
                new RefreshTokenFamilyId(entity.getFamilyId()),
                new TokenHash(entity.getTokenHash()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getRotatedAt(),
                entity.getRevokedAt());
    }
}
