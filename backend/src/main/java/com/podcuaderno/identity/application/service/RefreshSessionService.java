package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.RefreshSessionCommand;
import com.podcuaderno.identity.application.port.in.RefreshSessionUseCase;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.application.port.out.AccessTokenIssuer;
import com.podcuaderno.identity.application.port.out.IssuedAccessToken;
import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.exception.InvalidRefreshTokenException;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.identity.domain.model.User;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = InvalidRefreshTokenException.class) // keeps the family revocation when rejecting reuse
class RefreshSessionService implements RefreshSessionUseCase {

    private final RefreshTokenRepository refreshTokens;
    private final UserRepository users;
    private final AccessTokenIssuer accessTokens;
    private final Clock clock;

    RefreshSessionService(
            RefreshTokenRepository refreshTokens, UserRepository users, AccessTokenIssuer accessTokens, Clock clock) {
        this.refreshTokens = refreshTokens;
        this.users = users;
        this.accessTokens = accessTokens;
        this.clock = clock;
    }

    @Override
    public SessionTokens refresh(RefreshSessionCommand command) {
        Instant now = clock.instant();
        RefreshToken current = refreshTokens
                .findByHashForUpdate(TokenHash.of(command.refreshToken()))
                .orElseThrow(InvalidRefreshTokenException::new);
        if (current.wasRotated()) {
            // Reuse of an old token: assume it was stolen and close every session of the family (ADR-0009).
            refreshTokens.revokeFamily(current.familyId(), now);
            throw new InvalidRefreshTokenException();
        }
        if (!current.isUsable(now)) {
            throw new InvalidRefreshTokenException();
        }
        User user =
                users.findById(current.userId()).filter(User::isActive).orElseThrow(InvalidRefreshTokenException::new);
        OpaqueToken raw = OpaqueToken.generate();
        RefreshToken next = current.rotate(raw.hash(), now);
        refreshTokens.save(current);
        refreshTokens.save(next);
        IssuedAccessToken access = accessTokens.issue(user.id(), user.role(), now);
        return new SessionTokens(access.value(), access.expiresAt(), raw.value(), next.expiresAt());
    }
}
