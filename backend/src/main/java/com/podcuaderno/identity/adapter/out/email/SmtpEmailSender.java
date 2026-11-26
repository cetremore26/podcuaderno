package com.podcuaderno.identity.adapter.out.email;

import com.podcuaderno.identity.application.port.out.EmailSender;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import java.time.Instant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Plain-text emails in Spanish through SMTP (Mailpit locally). Never logs the link: it contains the token. */
@Component
@EnableConfigurationProperties(MailProperties.class)
class SmtpEmailSender implements EmailSender {

    static final String VERIFICATION_SUBJECT = "Activa tu cuenta de Podcuaderno";
    static final String VERIFICATION_PATH = "/verificar-correo#token=";

    private static final String VERIFICATION_TEXT = """
            Hola:

            Gracias por crear tu cuenta en Podcuaderno. Para activarla, abre este enlace:

            %s

            El enlace vence en 24 horas y solo sirve una vez. Si no creaste esta cuenta, ignora este mensaje.

            Equipo de Podcuaderno
            """;

    private final JavaMailSender mailSender;
    private final MailProperties properties;

    SmtpEmailSender(JavaMailSender mailSender, MailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void sendVerificationEmail(EmailAddress to, OpaqueToken token, Instant expiresAt) {
        // The token goes in the fragment so it never reaches server logs or the Referer header (ADR-0009).
        String link = properties.frontendUrl() + VERIFICATION_PATH + token.value();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.from());
        message.setTo(to.value());
        message.setSubject(VERIFICATION_SUBJECT);
        message.setText(VERIFICATION_TEXT.formatted(link));
        mailSender.send(message);
    }
}
