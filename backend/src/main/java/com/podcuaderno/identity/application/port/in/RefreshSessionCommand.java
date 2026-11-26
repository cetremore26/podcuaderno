package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record RefreshSessionCommand(String refreshToken) {

    public RefreshSessionCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    }

    @Override
    public String toString() {
        return "RefreshSessionCommand[***]";
    }
}
