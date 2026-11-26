package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.RegisterUserUseCase;
import com.podcuaderno.identity.application.port.in.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Registration", description = "Account creation and email verification (HU-01)")
class RegistrationController {

    private final RegisterUserUseCase registerUser;
    private final VerifyEmailUseCase verifyEmail;

    RegistrationController(RegisterUserUseCase registerUser, VerifyEmailUseCase verifyEmail) {
        this.registerUser = registerUser;
        this.verifyEmail = verifyEmail;
    }

    @PostMapping("/registrations")
    @Operation(
            summary = "Create a pending account",
            description = "Always answers 202 without a body, whether or not the email is already registered.")
    ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest body) {
        registerUser.register(body.toCommand());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/email-verifications/confirmations")
    @Operation(
            summary = "Activate an account with the token of the verification link",
            description = "Confirming an already used link of an active account answers 204 again.")
    ResponseEntity<Void> confirm(@Valid @RequestBody EmailConfirmationRequest body) {
        verifyEmail.verify(body.toCommand());
        return ResponseEntity.noContent().build();
    }
}
