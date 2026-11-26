package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.ResendEmailVerificationCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record EmailRequest(@NotBlank @Email @Size(max = 254) String email) {

    ResendEmailVerificationCommand toResendCommand() {
        return new ResendEmailVerificationCommand(email);
    }

    @Override
    public String toString() {
        return "EmailRequest[***]";
    }
}
