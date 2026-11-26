package com.podcuaderno.identity.adapter.in.web;

/** Body of a successful login or renewal; the refresh token travels only in the cookie. */
record AccessTokenResponse(String accessToken, String tokenType, long expiresIn) {

    static final String BEARER = "Bearer";

    static AccessTokenResponse bearer(String accessToken, long expiresInSeconds) {
        return new AccessTokenResponse(accessToken, BEARER, expiresInSeconds);
    }

    @Override
    public String toString() {
        return "AccessTokenResponse[***]";
    }
}
