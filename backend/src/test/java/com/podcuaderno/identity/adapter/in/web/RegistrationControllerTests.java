package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.podcuaderno.identity.application.port.in.RegisterUserCommand;
import com.podcuaderno.identity.application.port.in.RegisterUserUseCase;
import com.podcuaderno.identity.domain.exception.OutdatedPrivacyPolicyException;
import com.podcuaderno.shared.adapter.in.web.ApiSecurityTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(RegistrationController.class)
@Import(ApiSecurityTestConfiguration.class)
class RegistrationControllerTests {

    private static final String VALID_BODY = """
            {"email": "ana@correo.com", "password": "contraseña-segura",
             "privacyPolicyAccepted": true, "privacyPolicyVersion": "1.0"}
            """;

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private RegisterUserUseCase registerUser;

    private MvcTestResult post(String body) {
        return mvc.post()
                .uri("/api/v1/auth/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }

    @Test
    void acceptsTheRegistrationWithoutABody() {
        MvcTestResult result = post(VALID_BODY);

        assertThat(result).hasStatus(HttpStatus.ACCEPTED);
        assertThat(result).bodyText().isEmpty();
        verify(registerUser).register(new RegisterUserCommand("ana@correo.com", "contraseña-segura", "1.0"));
    }

    @Test
    void rejectsARegistrationWithoutAcceptingThePolicy() {
        MvcTestResult result =
                post(VALID_BODY.replace("\"privacyPolicyAccepted\": true", "\"privacyPolicyAccepted\": false"));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.type").isEqualTo("/errors/validation");
        verify(registerUser, never()).register(any());
    }

    @Test
    void rejectsAMalformedEmail() {
        MvcTestResult result = post(VALID_BODY.replace("ana@correo.com", "ana"));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        verify(registerUser, never()).register(any());
    }

    @Test
    void rejectsAShortPassword() {
        MvcTestResult result = post(VALID_BODY.replace("contraseña-segura", "corta"));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void reportsBusinessRulesWithTheirCode() {
        doThrow(new OutdatedPrivacyPolicyException()).when(registerUser).register(any());

        MvcTestResult result = post(VALID_BODY.replace("\"1.0\"", "\"0.9\""));

        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(result).bodyJson().extractingPath("$.code").isEqualTo("outdated-privacy-policy");
    }
}
