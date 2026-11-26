package com.podcuaderno;

import com.podcuaderno.identity.application.port.out.EmailSender;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/** Replaces SMTP in integration tests and keeps every email, so tests can follow the verification link. */
public class RecordingEmailSender implements EmailSender {

    public record SentEmail(String to, String token, Instant expiresAt) {}

    private final List<SentEmail> sent = new CopyOnWriteArrayList<>();

    @Override
    public void sendVerificationEmail(EmailAddress to, OpaqueToken token, Instant expiresAt) {
        sent.add(new SentEmail(to.value(), token.value(), expiresAt));
    }

    public List<SentEmail> sentTo(String email) {
        return sent.stream().filter(mail -> mail.to().equals(email)).toList();
    }

    public Optional<String> lastTokenFor(String email) {
        List<SentEmail> emails = sentTo(email);
        return emails.isEmpty()
                ? Optional.empty()
                : Optional.of(emails.getLast().token());
    }
}
