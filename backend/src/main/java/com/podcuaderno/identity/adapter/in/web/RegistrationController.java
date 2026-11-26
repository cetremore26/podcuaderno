package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.RegisterUserUseCase;
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

    RegistrationController(RegisterUserUseCase registerUser) {
        this.registerUser = registerUser;
    }

    @PostMapping("/registrations")
    @Operation(
            summary = "Create a pending account",
            description = "Always answers 202 without a body, whether or not the email is already registered.")
    ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest body) {
        registerUser.register(body.toCommand());
        return ResponseEntity.accepted().build();
    }
}
