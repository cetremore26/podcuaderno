package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.AccessTokenIssuer;
import com.podcuaderno.identity.application.port.out.IssuedAccessToken;
import com.podcuaderno.identity.domain.model.AccessTokenPolicy;
import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;

/** Readable fake of {@link AccessTokenIssuer} for service tests. */
class FakeAccessTokenIssuer implements AccessTokenIssuer {

    @Override
    public IssuedAccessToken issue(UserId userId, Role role, Instant now) {
        return new IssuedAccessToken(
                "access:" + userId.value() + ":" + role + ":" + now, now.plus(AccessTokenPolicy.TIME_TO_LIVE));
    }
}
