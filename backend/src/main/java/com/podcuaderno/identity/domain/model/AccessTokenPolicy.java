package com.podcuaderno.identity.domain.model;

import java.time.Duration;

/** Access tokens live 15 minutes (RNF-01, HU-02). */
public final class AccessTokenPolicy {

    public static final Duration TIME_TO_LIVE = Duration.ofMinutes(15);

    private AccessTokenPolicy() {}
}
