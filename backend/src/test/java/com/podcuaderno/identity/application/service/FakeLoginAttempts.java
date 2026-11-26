package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.LoginAttemptTracker;
import com.podcuaderno.identity.domain.model.EmailAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Simple fake of {@link LoginAttemptTracker} for service tests. */
class FakeLoginAttempts implements LoginAttemptTracker {

    private final Map<EmailAddress, List<Instant>> failures = new HashMap<>();

    @Override
    public List<Instant> failuresSince(EmailAddress email, Instant since) {
        return failures.getOrDefault(email, List.of()).stream()
                .filter(at -> at.isAfter(since))
                .toList();
    }

    @Override
    public void recordFailure(EmailAddress email, Instant at) {
        failures.computeIfAbsent(email, key -> new ArrayList<>()).add(at);
    }

    @Override
    public void clear(EmailAddress email) {
        failures.remove(email);
    }

    int failuresOf(EmailAddress email) {
        return failures.getOrDefault(email, List.of()).size();
    }
}
