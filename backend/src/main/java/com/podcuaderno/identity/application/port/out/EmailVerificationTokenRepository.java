package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Optional;

public interface EmailVerificationTokenRepository {

    void save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByHash(TokenHash hash);

    /** When the most recent token of the user was issued, to enforce the resend cooldown. */
    Optional<Instant> findLatestIssuedAt(UserId userId);
}
