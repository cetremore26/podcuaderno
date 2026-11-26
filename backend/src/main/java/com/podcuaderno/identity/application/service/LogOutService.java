package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.LogOutCommand;
import com.podcuaderno.identity.application.port.in.LogOutUseCase;
import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.domain.model.TokenHash;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class LogOutService implements LogOutUseCase {

    private final RefreshTokenRepository refreshTokens;
    private final Clock clock;

    LogOutService(RefreshTokenRepository refreshTokens, Clock clock) {
        this.refreshTokens = refreshTokens;
        this.clock = clock;
    }

    @Override
    public void logOut(LogOutCommand command) {
        refreshTokens
                .findByHash(TokenHash.of(command.refreshToken()))
                .ifPresent(token -> refreshTokens.revokeFamily(token.familyId(), clock.instant()));
    }
}
