package com.podcuaderno.identity.adapter.out.email;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings of the transactional emails.
 *
 * @param from sender address
 * @param frontendUrl public URL of the web app, used to build the links
 */
@ConfigurationProperties("podcuaderno.mail")
record MailProperties(String from, String frontendUrl) {

    MailProperties {
        Objects.requireNonNull(from, "podcuaderno.mail.from must be set");
        Objects.requireNonNull(frontendUrl, "podcuaderno.mail.frontend-url must be set");
        frontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
    }
}
