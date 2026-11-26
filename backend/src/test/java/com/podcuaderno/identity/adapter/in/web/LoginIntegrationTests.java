package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.jayway.jsonpath.JsonPath;
import com.podcuaderno.IntegrationTest;
import com.podcuaderno.RecordingEmailSender;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class LoginIntegrationTests {

    private static final String PASSWORD = "contraseña-segura";

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

    private String registeredEmail(boolean verified) {
        String email = UUID.randomUUID() + "@correo.com";
        post("/api/v1/auth/registrations", """
                {"email": "%s", "password": "%s", "privacyPolicyAccepted": true, "privacyPolicyVersion": "1.0"}
                """.formatted(email, PASSWORD));
        await().atMost(Duration.ofSeconds(10))
                .until(() -> emails.lastTokenFor(email).isPresent());
        if (verified) {
            post(
                    "/api/v1/auth/email-verifications/confirmations",
                    "{\"token\": \"%s\"}".formatted(emails.lastTokenFor(email).orElseThrow()));
        }
        return email;
    }

    private MvcTestResult logIn(String email, String password) {
        return post("/api/v1/auth/login", "{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password));
    }

    @Test
    void aVerifiedStudentLogsInAndTheAccessTokenOpensTheApi() throws Exception {
        String email = registeredEmail(true);

        MvcTestResult result = logIn(email, PASSWORD);

        assertThat(result).hasStatusOk();
        assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE)).startsWith("pc_refresh=");
        String accessToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        // No notebook endpoint yet: anything but 401 proves the token is accepted.
        assertThat(mvc.get().uri("/api/v1/notebooks").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .hasStatus(HttpStatus.NOT_FOUND);
        Integer refreshTokens = jdbc.queryForObject(
                "select count(*) from refresh_tokens r join users u on u.id = r.user_id where u.email = ?",
                Integer.class,
                email);
        assertThat(refreshTokens).isOne();
    }

    @Test
    void aWrongPasswordAndAnUnknownEmailLookTheSame() throws Exception {
        String email = registeredEmail(true);

        MvcTestResult wrongPassword = logIn(email, "otra-contraseña");
        MvcTestResult unknownEmail = logIn(UUID.randomUUID() + "@correo.com", PASSWORD);

        assertThat(wrongPassword).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(unknownEmail).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(wrongPassword.getResponse().getContentAsString())
                .isEqualTo(unknownEmail.getResponse().getContentAsString());
    }

    @Test
    void aPendingAccountIsAskedToVerifyItsEmail() {
        String email = registeredEmail(false);

        assertThat(logIn(email, PASSWORD))
                .hasStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("email-not-verified");
    }

    @Test
    void fiveFailuresBlockTheAccountWith429() {
        String email = registeredEmail(true);
        for (int i = 0; i < 5; i++) {
            assertThat(logIn(email, "mala-contraseña")).hasStatus(HttpStatus.UNAUTHORIZED);
        }

        MvcTestResult blocked = logIn(email, PASSWORD);

        assertThat(blocked).hasStatus(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(blocked).containsHeader(HttpHeaders.RETRY_AFTER);
    }
}
