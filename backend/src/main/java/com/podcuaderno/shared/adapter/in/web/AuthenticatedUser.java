package com.podcuaderno.shared.adapter.in.web;

import com.podcuaderno.shared.domain.UserId;
import java.util.Objects;

/**
 * Principal of every authenticated request. Controllers receive it with {@code @AuthenticationPrincipal} and pass
 * {@link #id()} to the use cases, which never see Spring Security types (docs/05 §5.8).
 */
public record AuthenticatedUser(UserId id) {

    public AuthenticatedUser {
        Objects.requireNonNull(id, "id must not be null");
    }
}
