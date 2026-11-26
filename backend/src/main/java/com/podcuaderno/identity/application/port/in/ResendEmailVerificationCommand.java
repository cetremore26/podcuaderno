package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record ResendEmailVerificationCommand(String email) {

    public ResendEmailVerificationCommand {
        Objects.requireNonNull(email, "email must not be null");
    }

    @Override
    public String toString() {
        return "ResendEmailVerificationCommand[***]";
    }
}
