package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.EmailSender;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Fake of {@link EmailSender} that keeps the sent emails, for service tests. */
class FakeEmailSender implements EmailSender {

    record SentEmail(EmailAddress to, OpaqueToken token, Instant expiresAt) {}

    private final List<SentEmail> sent = new ArrayList<>();

    @Override
    public void sendVerificationEmail(EmailAddress to, OpaqueToken token, Instant expiresAt) {
        sent.add(new SentEmail(to, token, expiresAt));
    }

    List<SentEmail> sent() {
        return List.copyOf(sent);
    }
}
