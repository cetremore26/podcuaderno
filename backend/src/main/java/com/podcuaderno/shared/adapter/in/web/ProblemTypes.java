package com.podcuaderno.shared.adapter.in.web;

import java.net.URI;

/** Values of the {@code type} field of the Problem Details returned by the API (docs/05 §5.6). */
public final class ProblemTypes {

    public static final URI VALIDATION = URI.create("/errors/validation");
    public static final URI UNAUTHENTICATED = URI.create("/errors/unauthenticated");
    public static final URI NOT_FOUND = URI.create("/errors/not-found");
    public static final URI BUSINESS_RULE = URI.create("/errors/business-rule");
    public static final URI INTERNAL = URI.create("/errors/internal");

    private ProblemTypes() {}
}
