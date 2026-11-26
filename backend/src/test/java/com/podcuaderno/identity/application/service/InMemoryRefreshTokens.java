package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.RefreshTokenId;
import com.podcuaderno.identity.domain.model.TokenHash;
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

    Optional<RefreshToken> withHash(TokenHash hash) {
        return tokens.values().stream()
                .filter(token -> token.tokenHash().equals(hash))
                .findFirst();
    }

    List<RefreshToken> all() {
        return List.copyOf(tokens.values());
    }
}
