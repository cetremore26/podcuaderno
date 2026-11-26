package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RefreshTokenTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final TokenHash HASH = TokenHash.of("raw");

    @Test
    void aNewTokenStartsAFamilyAndLasts14Days() {
        UserId user = UserId.newId();

        RefreshToken token = RefreshToken.issue(user, HASH, NOW);

        assertThat(token.userId()).isEqualTo(user);
        assertThat(token.tokenHash()).isEqualTo(HASH);
        assertThat(token.createdAt()).isEqualTo(NOW);
        assertThat(token.expiresAt()).isEqualTo(NOW.plus(Duration.ofDays(14)));
        assertThat(token.isUsable(NOW.plus(Duration.ofDays(14)).minusSeconds(1)))
                .isTrue();
        assertThat(token.isUsable(NOW.plus(Duration.ofDays(14)))).isFalse();
        assertThat(token.wasRotated()).isFalse();
        assertThat(token.rotatedAt()).isEmpty();
        assertThat(token.revokedAt()).isEmpty();
        assertThat(RefreshToken.issue(user, HASH, NOW).familyId()).isNotEqualTo(token.familyId());
    }

    @Test
    void restoredRotatedOrRevokedTokensAreNotUsable() {
        RefreshTokenId id = RefreshTokenId.newId();
        RefreshTokenFamilyId family = RefreshTokenFamilyId.newId();
        Instant expiry = NOW.plus(Duration.ofDays(1));

        RefreshToken rotated =
                RefreshToken.restore(id, UserId.newId(), family, HASH, NOW, expiry, NOW.plusSeconds(5), null);
        RefreshToken revoked =
                RefreshToken.restore(id, UserId.newId(), family, HASH, NOW, expiry, null, NOW.plusSeconds(5));

        assertThat(rotated.id()).isEqualTo(id);
        assertThat(rotated.familyId()).isEqualTo(family);
        assertThat(rotated.wasRotated()).isTrue();
        assertThat(rotated.isUsable(NOW.plusSeconds(10))).isFalse();
        assertThat(revoked.wasRotated()).isFalse();
        assertThat(revoked.revokedAt()).contains(NOW.plusSeconds(5));
        assertThat(revoked.isUsable(NOW.plusSeconds(10))).isFalse();
    }

    @Test
    void requiresItsMandatoryFields() {
        RefreshTokenId id = RefreshTokenId.newId();
        RefreshTokenFamilyId family = RefreshTokenFamilyId.newId();
        UserId user = UserId.newId();

        assertThatThrownBy(() -> RefreshToken.restore(null, user, family, HASH, NOW, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RefreshToken.restore(id, null, family, HASH, NOW, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RefreshToken.restore(id, user, null, HASH, NOW, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RefreshToken.restore(id, user, family, null, NOW, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RefreshToken.restore(id, user, family, HASH, null, NOW, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RefreshToken.restore(id, user, family, HASH, NOW, null, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new RefreshTokenId(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new RefreshTokenFamilyId(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void accessTokensLive15Minutes() {
        assertThat(AccessTokenPolicy.TIME_TO_LIVE).isEqualTo(Duration.ofMinutes(15));
    }
}
