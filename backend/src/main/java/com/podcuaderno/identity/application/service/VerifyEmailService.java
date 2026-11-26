package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.VerifyEmailCommand;
import com.podcuaderno.identity.application.port.in.VerifyEmailUseCase;
import com.podcuaderno.identity.application.port.out.EmailVerificationTokenRepository;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.exception.InvalidVerificationTokenException;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.identity.domain.model.User;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class VerifyEmailService implements VerifyEmailUseCase {

    private final UserRepository users;
    private final EmailVerificationTokenRepository tokens;
    private final Clock clock;

    VerifyEmailService(UserRepository users, EmailVerificationTokenRepository tokens, Clock clock) {
        this.users = users;
        this.tokens = tokens;
        this.clock = clock;
    }

    @Override
    public void verify(VerifyEmailCommand command) {
        Instant now = clock.instant();
        EmailVerificationToken token =
                tokens.findByHash(TokenHash.of(command.token())).orElseThrow(InvalidVerificationTokenException::new);
        User user = users.findById(token.userId()).orElseThrow(InvalidVerificationTokenException::new);
        if (token.isUsed() && user.isActive()) {
            return; // the same link opened twice: idempotent
        }
        token.markUsed(now);
        user.verifyEmail(now);
        tokens.save(token);
        users.save(user);
    }
}
