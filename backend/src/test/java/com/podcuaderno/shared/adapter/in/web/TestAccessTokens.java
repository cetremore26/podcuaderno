package com.podcuaderno.shared.adapter.in.web;

import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

/** Issues real access tokens for tests, signed with the application's {@link JwtEncoder}. */
public final class TestAccessTokens {

    private TestAccessTokens() {}

    public static String issue(JwtEncoder encoder, UserId userId, Instant issuedAt, Duration timeToLive) {
        return issue(encoder, userId.value().toString(), issuedAt, timeToLive);
    }

    public static String issue(JwtEncoder encoder, UserId userId) {
        return issue(encoder, userId, Instant.now(), Duration.ofMinutes(15));
    }

    static String issue(JwtEncoder encoder, String subject, Instant issuedAt, Duration timeToLive) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(timeToLive))
                .claim("role", "STUDENT")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
