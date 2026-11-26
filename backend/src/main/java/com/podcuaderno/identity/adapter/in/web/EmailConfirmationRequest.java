package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.VerifyEmailCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record EmailConfirmationRequest(@NotBlank @Size(max = 100) String token) {

    VerifyEmailCommand toCommand() {
        return new VerifyEmailCommand(token);
    }

    @Override
    public String toString() {
        return "EmailConfirmationRequest[***]";
    }
}
