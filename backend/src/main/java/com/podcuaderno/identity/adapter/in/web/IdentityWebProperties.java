package com.podcuaderno.identity.adapter.in.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Web settings of the identity module.
 *
 * @param refreshCookieSecure whether the refresh cookie is {@code Secure}; only Safari on plain-http localhost needs
 *     {@code false} (ADR-0009)
 */
@ConfigurationProperties("podcuaderno.identity")
record IdentityWebProperties(@DefaultValue("true") boolean refreshCookieSecure) {}
