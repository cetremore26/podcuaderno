package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.IntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class RegistrationIntegrationTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JdbcTemplate jdbc;

    private MvcTestResult register(String email, String password) {
        return mvc.post()
                .uri("/api/v1/auth/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "password": "%s", "privacyPolicyAccepted": true, "privacyPolicyVersion": "1.0"}
                        """.formatted(email, password))
                .exchange();
    }

    @Test
    void registeringTheSameEmailTwiceLooksIdenticalAndStoresOneAccount() {
        String email = UUID.randomUUID() + "@correo.com";

        MvcTestResult first = register(email, "contraseña-segura");
        MvcTestResult second = register(email.toUpperCase(java.util.Locale.ROOT), "otra-contraseña");

        assertThat(first).hasStatus(HttpStatus.ACCEPTED);
        assertThat(second).hasStatus(HttpStatus.ACCEPTED);
        assertThat(second.getResponse().getContentAsByteArray())
                .isEqualTo(first.getResponse().getContentAsByteArray());
        Integer accounts = jdbc.queryForObject("select count(*) from users where email = ?", Integer.class, email);
        assertThat(accounts).isOne();
    }

    @Test
    void storesThePendingAccountWithTheAcceptedPolicyVersion() {
        String email = UUID.randomUUID() + "@correo.com";

        register(email, "contraseña-segura");

        String status = jdbc.queryForObject("select status from users where email = ?", String.class, email);
        String version = jdbc.queryForObject(
                "select c.policy_version from consents c join users u on u.id = c.user_id where u.email = ?",
                String.class,
                email);
        String hash = jdbc.queryForObject("select password_hash from users where email = ?", String.class, email);
        assertThat(status).isEqualTo("PENDING_VERIFICATION");
        assertThat(version).isEqualTo("1.0");
        assertThat(hash).startsWith("{argon2id}").doesNotContain("contraseña-segura");
    }
}
