package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.EmailVerificationTokenRepository;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.EmailVerificationTokenId;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** In-memory fake of {@link EmailVerificationTokenRepository} for service tests. */
class InMemoryEmailVerificationTokens implements EmailVerificationTokenRepository {

    private final Map<EmailVerificationTokenId, EmailVerificationToken> tokens = new LinkedHashMap<>();

    @Override
    public void save(EmailVerificationToken token) {
        tokens.put(token.id(), token);
    }

    @Override
    public Optional<EmailVerificationToken> findByHash(TokenHash hash) {
        return tokens.values().stream()
                .filter(token -> token.tokenHash().equals(hash))
                .findFirst();
    }

    @Override
    public Optional<Instant> findLatestIssuedAt(UserId userId) {
        return tokens.values().stream()
                .filter(token -> token.userId().equals(userId))
                .map(EmailVerificationToken::createdAt)
                .max(Comparator.naturalOrder());
    }

    int count() {
        return tokens.size();
    }
}
