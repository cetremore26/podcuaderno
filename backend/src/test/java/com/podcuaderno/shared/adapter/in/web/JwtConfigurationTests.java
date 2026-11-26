package com.podcuaderno.shared.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtConfigurationTests {

    private final JwtConfiguration configuration = new JwtConfiguration();

    @Test
    void rejectsASecretShorterThan32Bytes() {
        String shortSecret = Base64.getEncoder().encodeToString(new byte[31]);

        assertThatThrownBy(() -> configuration.accessTokenSigningKey(new SecurityProperties(shortSecret, List.of())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    @Test
    void usesAConfiguredSecretOf32Bytes() {
        byte[] secret = new byte[32];
        secret[0] = 7;

        SecretKey key = configuration.accessTokenSigningKey(
                new SecurityProperties(Base64.getEncoder().encodeToString(secret), List.of()));

        assertThat(key.getEncoded()).isEqualTo(secret);
        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
    }

    @Test
    void generatesAUsableRandomKeyWhenNoSecretIsConfigured() {
        SecretKey key = configuration.accessTokenSigningKey(new SecurityProperties(" ", List.of()));
        UserId user = UserId.newId();

        String token = TestAccessTokens.issue(configuration.jwtEncoder(key), user);
        Jwt decoded = configuration.jwtDecoder(key).decode(token);

        assertThat(key.getEncoded()).hasSize(32);
        assertThat(decoded.getSubject()).isEqualTo(user.value().toString());
    }

    @Test
    void missingOriginsMeanNoOriginIsAllowed() {
        assertThat(new SecurityProperties(null, null).allowedOrigins()).isEmpty();
    }
}
