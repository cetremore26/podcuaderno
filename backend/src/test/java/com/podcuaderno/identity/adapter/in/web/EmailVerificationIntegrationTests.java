package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.podcuaderno.IntegrationTest;
import com.podcuaderno.RecordingEmailSender;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class EmailVerificationIntegrationTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private RecordingEmailSender emails;

    @Autowired
    private JdbcTemplate jdbc;

    private MvcTestResult post(String uri, String body) {
        return mvc.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }

    private String registerAndWaitForTheLink(String email) {
        post("/api/v1/auth/registrations", """
                {"email": "%s", "password": "contraseña-segura", "privacyPolicyAccepted": true, "privacyPolicyVersion": "1.0"}
                """.formatted(email));
        await().atMost(Duration.ofSeconds(10))
                .until(() -> emails.lastTokenFor(email).isPresent());
        return emails.lastTokenFor(email).orElseThrow();
    }

    private MvcTestResult confirm(String token) {
        return post("/api/v1/auth/email-verifications/confirmations", "{\"token\": \"%s\"}".formatted(token));
    }

    @Test
    void theEmailedLinkActivatesTheAccountAndCanBeOpenedTwice() {
        String email = UUID.randomUUID() + "@correo.com";
        String token = registerAndWaitForTheLink(email);

        assertThat(confirm(token)).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(confirm(token)).hasStatus(HttpStatus.NO_CONTENT);

        String status = jdbc.queryForObject("select status from users where email = ?", String.class, email);
        assertThat(status).isEqualTo("ACTIVE");
        Integer storedClearTokens = jdbc.queryForObject(
                "select count(*) from email_verification_tokens where token_hash = ?", Integer.class, token);
        assertThat(storedClearTokens).isZero();
    }

    @Test
    void anInventedTokenIsRejectedWithItsCode() {
        MvcTestResult result = confirm("invented-token");

        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(result).bodyJson().extractingPath("$.code").isEqualTo("invalid-verification-token");
    }

    @Test
    void resendingForAnUnknownEmailAnswers202AndSendsNothing() {
        String email = UUID.randomUUID() + "@correo.com";

        MvcTestResult result = post("/api/v1/auth/email-verifications", "{\"email\": \"%s\"}".formatted(email));

        assertThat(result).hasStatus(HttpStatus.ACCEPTED);
        assertThat(emails.sentTo(email)).isEmpty();
    }
}
