package com.podcuaderno.identity.adapter.out.security;

import com.podcuaderno.identity.application.port.out.AccessTokenIssuer;
import com.podcuaderno.identity.application.port.out.IssuedAccessToken;
import com.podcuaderno.identity.domain.model.AccessTokenPolicy;
import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/** Signs HS256 access tokens with the {@link JwtEncoder} bean of the shared kernel (ADR-0009). */
@Component
class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private static final String ROLE_CLAIM = "role";

    private final JwtEncoder encoder;

    JwtAccessTokenIssuer(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public IssuedAccessToken issue(UserId userId, Role role, Instant now) {
        Instant expiresAt = now.plus(AccessTokenPolicy.TIME_TO_LIVE);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.value().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim(ROLE_CLAIM, role.name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedAccessToken(value, expiresAt);
    }
}
