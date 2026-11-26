package com.podcuaderno.shared.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.shared.domain.UserId;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
@Import({SecurityConfiguration.class, JwtConfiguration.class, JwtAuthenticationTests.MeController.class})
class JwtAuthenticationTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void validTokenAuthenticatesTheUser() {
        UserId user = UserId.newId();
        String token = TestAccessTokens.issue(jwtEncoder, user);

        assertThat(mvc.get().uri("/test/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .hasStatusOk()
                .bodyText()
                .isEqualTo(user.value().toString());
    }

    @Test
    void expiredTokenIsRejected() {
        String token = TestAccessTokens.issue(
                jwtEncoder, UserId.newId(), Instant.now().minus(Duration.ofHours(1)), Duration.ofMinutes(15));

        assertUnauthenticated(mvc.get()
                .uri("/test/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange());
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        byte[] otherKey = new byte[32];
        new SecureRandom().nextBytes(otherKey);
        JwtEncoder otherEncoder = NimbusJwtEncoder.withSecretKey(new SecretKeySpec(otherKey, "HmacSHA256"))
                .algorithm(MacAlgorithm.HS256)
                .build();
        String token = TestAccessTokens.issue(otherEncoder, UserId.newId());

        assertUnauthenticated(mvc.get()
                .uri("/test/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange());
    }

    @Test
    void tokenWhoseSubjectIsNotAUuidIsRejected() {
        String token = TestAccessTokens.issue(jwtEncoder, "ana@correo.com", Instant.now(), Duration.ofMinutes(15));

        assertUnauthenticated(mvc.get()
                .uri("/test/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange());
    }

    @Test
    void requestWithoutTokenIsRejected() {
        assertUnauthenticated(mvc.get().uri("/test/me").exchange());
    }

    @Test
    void authenticationEndpointsArePublic() {
        // No handler exists yet, so a public endpoint answers 404 instead of 401.
        assertThat(mvc.post().uri("/api/v1/auth/anything")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void preflightFromTheFrontendOriginIsAllowed() {
        MvcTestResult result = mvc.options()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PATCH")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type")
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173");
        assertThat(result).hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    void preflightFromAnUnknownOriginIsRejected() {
        MvcTestResult result = mvc.options()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.ORIGIN, "https://evil.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(result).doesNotContainHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    private static void assertUnauthenticated(MvcTestResult result) {
        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.type").isEqualTo("/errors/unauthenticated");
    }

    @RestController
    static class MeController {

        @GetMapping("/test/me")
        String me(@AuthenticationPrincipal AuthenticatedUser user) {
            return user.id().value().toString();
        }
    }
}
