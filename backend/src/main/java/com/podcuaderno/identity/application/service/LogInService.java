package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.LogInCommand;
import com.podcuaderno.identity.application.port.in.LogInUseCase;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.application.port.out.AccessTokenIssuer;
import com.podcuaderno.identity.application.port.out.IssuedAccessToken;
import com.podcuaderno.identity.application.port.out.LoginAttemptTracker;
import com.podcuaderno.identity.application.port.out.PasswordHasher;
import com.podcuaderno.identity.application.port.out.RefreshTokenRepository;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.exception.EmailNotVerifiedException;
import com.podcuaderno.identity.domain.exception.InvalidCredentialsException;
import com.podcuaderno.identity.domain.exception.TooManyLoginAttemptsException;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.LoginThrottle;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.User;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class LogInService implements LogInUseCase {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final LoginAttemptTracker attempts;
    private final AccessTokenIssuer accessTokens;
    private final RefreshTokenRepository refreshTokens;
    private final Clock clock;

    LogInService(
            UserRepository users,
            PasswordHasher hasher,
            LoginAttemptTracker attempts,
            AccessTokenIssuer accessTokens,
            RefreshTokenRepository refreshTokens,
            Clock clock) {
        this.users = users;
        this.hasher = hasher;
        this.attempts = attempts;
        this.accessTokens = accessTokens;
        this.refreshTokens = refreshTokens;
        this.clock = clock;
    }

    @Override
    public SessionTokens logIn(LogInCommand command) {
        Instant now = clock.instant();
        EmailAddress email = new EmailAddress(command.email());
        Optional<Duration> wait =
                LoginThrottle.retryAfter(attempts.failuresSince(email, now.minus(LoginThrottle.WINDOW)), now);
        if (wait.isPresent()) {
            throw new TooManyLoginAttemptsException(wait.get());
        }
        Optional<User> user = users.findByEmail(email);
        // Unknown emails are compared against a dummy hash, so the response time does not reveal them.
        PasswordHash hash = user.map(User::passwordHash).orElseGet(hasher::dummyHash);
        boolean passwordMatches = hasher.matches(command.password(), hash);
        if (user.isEmpty() || !passwordMatches) {
            attempts.recordFailure(email, now); // in memory: survives the rollback
            throw new InvalidCredentialsException();
        }
        User found = user.get();
        if (!found.isActive()) {
            throw new EmailNotVerifiedException();
        }
        attempts.clear(email);
        return startSession(found, now);
    }

    private SessionTokens startSession(User user, Instant now) {
        IssuedAccessToken access = accessTokens.issue(user.id(), user.role(), now);
        OpaqueToken raw = OpaqueToken.generate();
        RefreshToken refresh = RefreshToken.issue(user.id(), raw.hash(), now);
        refreshTokens.save(refresh);
        return new SessionTokens(access.value(), access.expiresAt(), raw.value(), refresh.expiresAt());
    }
}
