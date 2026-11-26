package com.podcuaderno.metrics.adapter.in.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.TestcontainersConfiguration;
import com.podcuaderno.identity.domain.event.UserRegistered;
import com.podcuaderno.notebooks.domain.event.NotebookCreated;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

/** The metrics module on its own: events of other modules become usage events without content (ADR-0003). */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
class UsageEventListenerModuleTests {

    @Autowired
    private JdbcTemplate jdbc;

    private int countOf(UserId user, String type) {
        Integer count = jdbc.queryForObject(
                "select count(*) from usage_events where user_id = ? and type = ?", Integer.class, user.value(), type);
        return count == null ? 0 : count;
    }

    private int charactersOf(UserId user) {
        Integer characters = jdbc.queryForObject(
                "select coalesce(sum(characters), 0) from usage_events where user_id = ?", Integer.class, user.value());
        return characters == null ? 0 : characters;
    }

    @Test
    void recordsARegistrationWithoutContent(Scenario scenario) {
        UserId user = UserId.newId();

        scenario.publish(new UserRegistered(user, Instant.now()))
                .andWaitForStateChange(() -> countOf(user, "USER_REGISTERED"), count -> count == 1)
                .andVerify(count -> assertThat(charactersOf(user)).isZero());
    }

    @Test
    void recordsANotebookCreation(Scenario scenario) {
        UserId owner = UserId.newId();

        scenario.publish(new NotebookCreated(UUID.randomUUID(), owner, Instant.now()))
                .andWaitForStateChange(() -> countOf(owner, "NOTEBOOK_CREATED"), count -> count == 1)
                .andVerify(count -> assertThat(charactersOf(owner)).isZero());
    }
}
