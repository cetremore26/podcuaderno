package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;

/** Signs short-lived access tokens (ADR-0009). */
public interface AccessTokenIssuer {

    IssuedAccessToken issue(UserId userId, Role role, Instant now);
}
