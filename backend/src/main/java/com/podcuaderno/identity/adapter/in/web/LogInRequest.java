package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.LogInCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record LogInRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(max = 128) String password) {

    LogInCommand toCommand() {
        return new LogInCommand(email, password);
    }

    @Override
    public String toString() {
        return "LogInRequest[***]";
    }
}
