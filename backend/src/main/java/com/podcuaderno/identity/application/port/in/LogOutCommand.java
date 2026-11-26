package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record LogOutCommand(String refreshToken) {

    public LogOutCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    }

    @Override
    public String toString() {
        return "LogOutCommand[***]";
    }
}
