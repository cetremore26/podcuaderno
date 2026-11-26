package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record LogInCommand(String email, String password) {

    public LogInCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
    }

    @Override
    public String toString() {
        return "LogInCommand[***]";
    }
}
