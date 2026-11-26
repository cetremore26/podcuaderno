package com.podcuaderno.identity.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.identity.application.port.out.IssuedAccessToken;
import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.shared.domain.UserId;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtAccessTokenIssuerTests {

    @Test
    void issuesAnHs256TokenWithSubjectRoleAnd15MinutesOfLife() {
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        SecretKey key = new SecretKeySpec(secret, "HmacSHA256");
        JwtAccessTokenIssuer issuer = new JwtAccessTokenIssuer(NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build());
        UserId user = UserId.newId();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        IssuedAccessToken token = issuer.issue(user, Role.STUDENT, now);

        Jwt decoded = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
                .decode(token.value());
        assertThat(decoded.getSubject()).isEqualTo(user.value().toString());
        assertThat(decoded.getClaimAsString("role")).isEqualTo("STUDENT");
        assertThat(Duration.between(decoded.getIssuedAt(), decoded.getExpiresAt()))
                .isEqualTo(Duration.ofSeconds(900));
        assertThat(token.expiresAt()).isEqualTo(now.plus(Duration.ofMinutes(15)));
        assertThat(token).hasToString("IssuedAccessToken[***]");
    }
}
