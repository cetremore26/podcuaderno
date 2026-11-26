package com.podcuaderno.identity.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.IntegrationTest;
import com.podcuaderno.identity.application.port.out.ConsentRepository;
import com.podcuaderno.identity.application.port.out.EmailVerificationTokenRepository;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.identity.domain.model.ConsentId;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.EmailVerificationTokenId;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.PrivacyPolicy;
import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.identity.domain.model.UserStatus;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class IdentityPersistenceIntegrationTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final PasswordHash HASH = new PasswordHash("{argon2id}hash");

    @Autowired
    private UserRepository users;

    @Autowired
    private ConsentRepository consents;

    @Autowired
    private EmailVerificationTokenRepository tokens;

    @Autowired
    private JdbcTemplate jdbc;

    private static EmailAddress uniqueEmail() {
        return new EmailAddress(UUID.randomUUID() + "@correo.com");
    }

    private User savedUser() {
        User user = User.register(UserId.newId(), uniqueEmail(), HASH, NOW);
        users.save(user);
        return user;
    }

    @Test
    void secondMigrationIsApplied() {
        Boolean success =
                jdbc.queryForObject("select success from flyway_schema_history where version = '2'", Boolean.class);

        assertThat(success).isTrue();
    }

    @Test
    void savesAndReadsAUser() {
        User user = savedUser();

        User found = users.findById(user.id()).orElseThrow();

        assertThat(found.email()).isEqualTo(user.email());
        assertThat(found.passwordHash()).isEqualTo(HASH);
        assertThat(found.status()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(found.role()).isEqualTo(Role.STUDENT);
        assertThat(found.createdAt()).isEqualTo(NOW);
        assertThat(found.emailVerifiedAt()).isEmpty();
        assertThat(users.findByEmail(user.email())).isPresent();
        assertThat(users.findById(UserId.newId())).isEmpty();
    }

    @Test
    void savingAgainUpdatesTheVerificationState() {
        User user = savedUser();
        user.verifyEmail(NOW.plusSeconds(60));

        users.save(user);

        User found = users.findById(user.id()).orElseThrow();
        assertThat(found.isActive()).isTrue();
        assertThat(found.emailVerifiedAt()).contains(NOW.plusSeconds(60));
    }

    @Test
    void emailIsUnique() {
        User user = savedUser();
        User duplicate = User.register(UserId.newId(), user.email(), HASH, NOW);

        assertThatThrownBy(() -> users.save(duplicate)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void existsByEmailUsesTheNormalisedAddress() {
        User user = savedUser();

        assertThat(users.existsByEmail(
                        new EmailAddress(" " + user.email().value().toUpperCase(Locale.ROOT) + " ")))
                .isTrue();
        assertThat(users.existsByEmail(uniqueEmail())).isFalse();
    }

    @Test
    void storesTheConsentWithVersionAndDate() {
        User user = savedUser();

        consents.save(Consent.accept(ConsentId.newId(), user.id(), PrivacyPolicy.CURRENT_VERSION, NOW));

        assertThat(consents.findByUserId(user.id())).singleElement().satisfies(consent -> {
            assertThat(consent.policyVersion()).isEqualTo("1.0");
            assertThat(consent.acceptedAt()).isEqualTo(NOW);
        });
    }

    @Test
    void findsTokensByHashAndTheLatestIssueDate() {
        User user = savedUser();
        TokenHash firstHash = TokenHash.of(UUID.randomUUID().toString());
        EmailVerificationToken first =
                EmailVerificationToken.issue(EmailVerificationTokenId.newId(), user.id(), firstHash, NOW);
        EmailVerificationToken second = EmailVerificationToken.issue(
                EmailVerificationTokenId.newId(),
                user.id(),
                TokenHash.of(UUID.randomUUID().toString()),
                NOW.plusSeconds(300));
        tokens.save(first);
        tokens.save(second);
        first.markUsed(NOW.plusSeconds(10));
        tokens.save(first);

        EmailVerificationToken found = tokens.findByHash(firstHash).orElseThrow();

        assertThat(found.id()).isEqualTo(first.id());
        assertThat(found.userId()).isEqualTo(user.id());
        assertThat(found.usedAt()).contains(NOW.plusSeconds(10));
        assertThat(found.expiresAt()).isEqualTo(first.expiresAt());
        assertThat(tokens.findLatestIssuedAt(user.id())).contains(NOW.plusSeconds(300));
        assertThat(tokens.findLatestIssuedAt(UserId.newId())).isEmpty();
        assertThat(tokens.findByHash(TokenHash.of("unknown"))).isEmpty();
    }

    @Test
    void deletingAUserCascadesToItsConsentsAndTokens() {
        User user = savedUser();
        consents.save(Consent.accept(ConsentId.newId(), user.id(), PrivacyPolicy.CURRENT_VERSION, NOW));
        tokens.save(EmailVerificationToken.issue(
                EmailVerificationTokenId.newId(),
                user.id(),
                TokenHash.of(UUID.randomUUID().toString()),
                NOW));

        jdbc.update("delete from users where id = ?", user.id().value());

        assertThat(consents.findByUserId(user.id())).isEmpty();
        assertThat(tokens.findLatestIssuedAt(user.id())).isEmpty();
    }
}
