package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.SendEmailVerificationCommand;
import com.podcuaderno.identity.application.port.in.SendEmailVerificationUseCase;
import com.podcuaderno.identity.application.port.out.EmailSender;
import com.podcuaderno.identity.application.port.out.EmailVerificationTokenRepository;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.EmailVerificationTokenId;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class SendEmailVerificationService implements SendEmailVerificationUseCase {

    private final UserRepository users;
    private final EmailVerificationTokenRepository tokens;
    private final EmailSender emailSender;
    private final Clock clock;

    SendEmailVerificationService(
            UserRepository users, EmailVerificationTokenRepository tokens, EmailSender emailSender, Clock clock) {
        this.users = users;
        this.tokens = tokens;
        this.emailSender = emailSender;
        this.clock = clock;
    }

    @Override
    public void send(SendEmailVerificationCommand command) {
        Instant now = clock.instant();
        users.findById(command.userId())
                .filter(User::isPendingVerification)
                .filter(user -> !recentlySent(user.id(), now))
                .ifPresent(user -> issueAndSend(user, now));
    }

    private boolean recentlySent(UserId userId, Instant now) {
        Instant cooldownStart = now.minus(EmailVerificationToken.RESEND_COOLDOWN);
        return tokens.findLatestIssuedAt(userId)
                .filter(issuedAt -> issuedAt.isAfter(cooldownStart))
                .isPresent();
    }

    private void issueAndSend(User user, Instant now) {
        OpaqueToken raw = OpaqueToken.generate(); // created here so the clear token never reaches event_publication
        EmailVerificationToken token =
                EmailVerificationToken.issue(EmailVerificationTokenId.newId(), user.id(), raw.hash(), now);
        tokens.save(token);
        // An SMTP failure rolls the token back and leaves the event incomplete, so Modulith retries it.
        emailSender.sendVerificationEmail(user.email(), raw, token.expiresAt());
    }
}
