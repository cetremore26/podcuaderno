package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record VerifyEmailCommand(String token) {

    public VerifyEmailCommand {
        Objects.requireNonNull(token, "token must not be null");
    }

    @Override
    public String toString() {
        return "VerifyEmailCommand[***]";
    }
}
