package com.podcuaderno.identity.adapter.out.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class SmtpEmailSenderTests {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);

    @Test
    void sendsThePlainTextVerificationEmailWithTheTokenInTheFragment() {
        SmtpEmailSender sender = new SmtpEmailSender(
                mailSender, new MailProperties("no-responder@podcuaderno.local", "http://localhost:3000/"));

        sender.sendVerificationEmail(
                new EmailAddress("ana@correo.com"), OpaqueToken.of("abc123"), Instant.parse("2026-12-15T10:00:00Z"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("ana@correo.com");
        assertThat(message.getFrom()).isEqualTo("no-responder@podcuaderno.local");
        assertThat(message.getSubject()).isEqualTo("Activa tu cuenta de Podcuaderno");
        assertThat(message.getText())
                .contains("http://localhost:3000/verificar-correo#token=abc123")
                .contains("vence en 24 horas")
                .startsWith("Hola:");
    }

    @Test
    void keepsAFrontendUrlWithoutTrailingSlash() {
        assertThat(new MailProperties("a@b.co", "https://podcuaderno.app").frontendUrl())
                .isEqualTo("https://podcuaderno.app");
    }
}
