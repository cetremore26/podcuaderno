package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.RefreshTokenFamilyId;
import com.podcuaderno.identity.domain.model.RefreshTokenId;
import com.podcuaderno.identity.domain.model.TokenHash;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** In-memory fake of {@link RefreshTokenRepository} for service tests. */
class InMemoryRefreshTokens implements RefreshTokenRepository {

    private final Map<RefreshTokenId, RefreshToken> tokens = new LinkedHashMap<>();

    @Override
    public void save(RefreshToken token) {
        tokens.put(token.id(), token);
    }

    @Override
    public Optional<RefreshToken> findByHash(TokenHash hash) {
        return tokens.values().stream()
                .filter(token -> token.tokenHash().equals(hash))
                .findFirst();
    }

    @Override
    public Optional<RefreshToken> findByHashForUpdate(TokenHash hash) {
        return findByHash(hash);
    }

    @Override
    public void revokeFamily(RefreshTokenFamilyId familyId, Instant now) {
        tokens.replaceAll((id, token) ->
                token.familyId().equals(familyId) && token.revokedAt().isEmpty()
                        ? RefreshToken.restore(
                                token.id(),
                                token.userId(),
                                token.familyId(),
                                token.tokenHash(),
                                token.createdAt(),
                                token.expiresAt(),
                                token.rotatedAt().orElse(null),
                                now)
                        : token);
    }

    Optional<RefreshToken> withHash(TokenHash hash) {
        return findByHash(hash);
    }

    List<RefreshToken> all() {
        return List.copyOf(tokens.values());
    }
}
