package com.podcuaderno.shared.adapter.in.web;

import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

/** Turns a validated access token into an {@link AuthenticatedUser} with its role as authority. */
final class AuthenticatedUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_CLAIM = "role";
    private static final String DEFAULT_ROLE = "STUDENT";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UserId id = new UserId(parseSubject(jwt.getSubject()));
        String role = Objects.requireNonNullElse(jwt.getClaimAsString(ROLE_CLAIM), DEFAULT_ROLE);
        return UsernamePasswordAuthenticationToken.authenticated(
                new AuthenticatedUser(id), jwt, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    private static UUID parseSubject(@Nullable String subject) {
        if (subject == null) {
            throw new InvalidBearerTokenException("Missing subject");
        }
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new InvalidBearerTokenException("Invalid subject", e);
        }
    }
}
