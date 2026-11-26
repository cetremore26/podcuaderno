package com.podcuaderno.identity.adapter.out.security;

import com.podcuaderno.identity.application.port.out.LoginAttemptTracker;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.LoginThrottle;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Failed logins kept in memory: one deployment, and a restart only resets the counters (ADR-0009). Old entries are
 * pruned when the map grows beyond {@value #PRUNE_THRESHOLD} emails.
 */
@Component
class InMemoryLoginAttemptTracker implements LoginAttemptTracker {

    static final int PRUNE_THRESHOLD = 10_000;

    private final Map<EmailAddress, List<Instant>> failures = new ConcurrentHashMap<>();

    @Override
    public List<Instant> failuresSince(EmailAddress email, Instant since) {
        return failures.getOrDefault(email, List.of()).stream()
                .filter(at -> at.isAfter(since))
                .toList();
    }

    @Override
    public void recordFailure(EmailAddress email, Instant at) {
        Instant windowStart = at.minus(LoginThrottle.WINDOW);
        failures.merge(
                email,
                List.of(at),
                (old, added) -> Stream.concat(old.stream().filter(time -> time.isAfter(windowStart)), added.stream())
                        .toList());
        if (failures.size() > PRUNE_THRESHOLD) {
            failures.values().removeIf(list -> list.stream().noneMatch(time -> time.isAfter(windowStart)));
        }
    }

    @Override
    public void clear(EmailAddress email) {
        failures.remove(email);
    }

    int trackedEmails() {
        return failures.size();
    }
}
