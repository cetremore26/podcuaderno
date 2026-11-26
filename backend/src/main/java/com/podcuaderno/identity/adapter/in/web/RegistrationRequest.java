package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.RegisterUserCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record RegistrationRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 8, max = 128) String password,
        @AssertTrue boolean privacyPolicyAccepted,
        @NotBlank @Size(max = 20) String privacyPolicyVersion) {

    RegisterUserCommand toCommand() {
        return new RegisterUserCommand(email, password, privacyPolicyVersion);
    }

    @Override
    public String toString() {
        return "RegistrationRequest[***]";
    }
}
