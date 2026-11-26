package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.domain.exception.InvalidVerificationTokenException;
import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class EmailVerificationTokenTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final TokenHash HASH = TokenHash.of("raw");

    private static EmailVerificationToken issued() {
        return EmailVerificationToken.issue(EmailVerificationTokenId.newId(), UserId.newId(), HASH, NOW);
    }

    @Test
    void expiresAfter24Hours() {
        EmailVerificationToken token = issued();
        Instant expiry = NOW.plus(Duration.ofHours(24));

        assertThat(token.createdAt()).isEqualTo(NOW);
        assertThat(token.expiresAt()).isEqualTo(expiry);
        assertThat(token.isUsable(expiry.minusSeconds(1))).isTrue();
        assertThat(token.isUsable(expiry)).isFalse();
        assertThat(token.tokenHash()).isEqualTo(HASH);
    }

    @Test
    void canBeUsedOnlyOnce() {
        EmailVerificationToken token = issued();
        Instant later = NOW.plusSeconds(60);

        token.markUsed(later);

        assertThat(token.isUsed()).isTrue();
        assertThat(token.usedAt()).contains(later);
        assertThat(token.isUsable(later)).isFalse();
        assertThatThrownBy(() -> token.markUsed(later.plusSeconds(1)))
                .isInstanceOf(InvalidVerificationTokenException.class)
                .extracting("code")
                .isEqualTo("invalid-verification-token");
    }

    @Test
    void anExpiredTokenCannotBeUsed() {
        EmailVerificationToken token = issued();

        assertThatThrownBy(() -> token.markUsed(NOW.plus(Duration.ofDays(2))))
                .isInstanceOf(InvalidVerificationTokenException.class);
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void restoreKeepsTheStoredState() {
        EmailVerificationTokenId id = EmailVerificationTokenId.newId();
        UserId user = UserId.newId();

        EmailVerificationToken token =
                EmailVerificationToken.restore(id, user, HASH, NOW, NOW.plusSeconds(30), NOW.plusSeconds(10));

        assertThat(token.id()).isEqualTo(id);
        assertThat(token.userId()).isEqualTo(user);
        assertThat(token.isUsed()).isTrue();
        assertThat(token.expiresAt()).isEqualTo(NOW.plusSeconds(30));
    }

    @Test
    void resendCooldownIsTwoMinutes() {
        assertThat(EmailVerificationToken.RESEND_COOLDOWN).isEqualTo(Duration.ofMinutes(2));
    }

    @Test
    void requiresItsMandatoryFields() {
        EmailVerificationTokenId id = EmailVerificationTokenId.newId();
        UserId user = UserId.newId();

        assertThatThrownBy(() -> EmailVerificationToken.restore(null, user, HASH, NOW, NOW, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> EmailVerificationToken.restore(id, null, HASH, NOW, NOW, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> EmailVerificationToken.restore(id, user, null, NOW, NOW, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> EmailVerificationToken.restore(id, user, HASH, null, NOW, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> EmailVerificationToken.restore(id, user, HASH, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
