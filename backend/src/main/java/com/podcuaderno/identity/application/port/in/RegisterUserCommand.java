package com.podcuaderno.identity.application.port.in;

import java.util.Objects;

public record RegisterUserCommand(String email, String password, String privacyPolicyVersion) {

    public RegisterUserCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
        Objects.requireNonNull(privacyPolicyVersion, "privacyPolicyVersion must not be null");
    }

    @Override
    public String toString() {
        return "RegisterUserCommand[***]"; // personal data and secrets stay out of logs (docs/05 §5.8)
    }
}
