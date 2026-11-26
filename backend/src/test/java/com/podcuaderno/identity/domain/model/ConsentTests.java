package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ConsentTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    @Test
    void recordsTheAcceptedVersionAndDate() {
        UserId user = UserId.newId();

        Consent consent = Consent.accept(ConsentId.newId(), user, PrivacyPolicy.CURRENT_VERSION, NOW);

        assertThat(consent.userId()).isEqualTo(user);
        assertThat(consent.policyVersion()).isEqualTo("1.0");
        assertThat(consent.acceptedAt()).isEqualTo(NOW);
    }

    @Test
    void requiresEveryField() {
        ConsentId id = ConsentId.newId();
        UserId user = UserId.newId();

        assertThatThrownBy(() -> Consent.accept(null, user, "1.0", NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Consent.accept(id, null, "1.0", NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Consent.accept(id, user, null, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Consent.accept(id, user, "1.0", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void identifiersAreGeneratedAndRequired() {
        assertThat(ConsentId.newId()).isNotEqualTo(ConsentId.newId());
        assertThat(EmailVerificationTokenId.newId()).isNotEqualTo(EmailVerificationTokenId.newId());
        assertThatThrownBy(() -> new ConsentId(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new EmailVerificationTokenId(null)).isInstanceOf(NullPointerException.class);
    }
}
