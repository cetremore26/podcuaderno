package com.podcuaderno.identity.application.port.in;

import com.podcuaderno.shared.domain.UserId;
import java.util.Objects;

public record SendEmailVerificationCommand(UserId userId) {

    public SendEmailVerificationCommand {
        Objects.requireNonNull(userId, "userId must not be null");
    }
}
