package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final EmailAddress EMAIL = new EmailAddress("ana@correo.com");
    private static final PasswordHash HASH = new PasswordHash("{argon2id}hash");

    @Test
    void aNewStudentIsPendingVerification() {
        UserId id = UserId.newId();

        User user = User.register(id, EMAIL, HASH, NOW);

        assertThat(user.id()).isEqualTo(id);
        assertThat(user.email()).isEqualTo(EMAIL);
        assertThat(user.passwordHash()).isEqualTo(HASH);
        assertThat(user.role()).isEqualTo(Role.STUDENT);
        assertThat(user.status()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(user.isPendingVerification()).isTrue();
        assertThat(user.isActive()).isFalse();
        assertThat(user.createdAt()).isEqualTo(NOW);
        assertThat(user.emailVerifiedAt()).isEmpty();
    }

    @Test
    void verifyingTheEmailActivatesTheAccount() {
        User user = User.register(UserId.newId(), EMAIL, HASH, NOW);
        Instant later = NOW.plus(Duration.ofHours(1));

        user.verifyEmail(later);

        assertThat(user.isActive()).isTrue();
        assertThat(user.isPendingVerification()).isFalse();
        assertThat(user.emailVerifiedAt()).contains(later);
    }

    @Test
    void verifyingAnActiveAccountAgainKeepsTheFirstDate() {
        User user = User.register(UserId.newId(), EMAIL, HASH, NOW);
        user.verifyEmail(NOW.plusSeconds(10));

        user.verifyEmail(NOW.plusSeconds(20));

        assertThat(user.emailVerifiedAt()).contains(NOW.plusSeconds(10));
    }

    @Test
    void restoreKeepsTheStoredState() {
        UserId id = UserId.newId();

        User user = User.restore(id, EMAIL, HASH, UserStatus.ACTIVE, Role.ADMIN, NOW, NOW.plusSeconds(5));

        assertThat(user.isActive()).isTrue();
        assertThat(user.role()).isEqualTo(Role.ADMIN);
        assertThat(user.emailVerifiedAt()).contains(NOW.plusSeconds(5));
    }

    @Test
    void requiresItsMandatoryFields() {
        UserId id = UserId.newId();

        assertThatThrownBy(() -> User.register(null, EMAIL, HASH, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> User.register(id, null, HASH, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> User.register(id, EMAIL, null, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> User.register(id, EMAIL, HASH, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> User.restore(id, EMAIL, HASH, null, Role.STUDENT, NOW, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> User.restore(id, EMAIL, HASH, UserStatus.ACTIVE, null, NOW, null))
                .isInstanceOf(NullPointerException.class);
    }
}
