package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.EmailAddress;
import java.time.Instant;
import java.util.List;

/** Remembers failed logins per email; the rule that uses them is {@code LoginThrottle}. */
public interface LoginAttemptTracker {

    List<Instant> failuresSince(EmailAddress email, Instant since);

    void recordFailure(EmailAddress email, Instant at);

    void clear(EmailAddress email);
}
