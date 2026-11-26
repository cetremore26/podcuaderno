package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import java.time.Instant;

/** Sends transactional emails to the user. */
public interface EmailSender {

    void sendVerificationEmail(EmailAddress to, OpaqueToken token, Instant expiresAt);
}
