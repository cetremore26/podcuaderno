package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.ConsentRepository;
import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.shared.domain.UserId;
import java.util.ArrayList;
import java.util.List;

/** In-memory fake of {@link ConsentRepository} for service tests. */
class InMemoryConsents implements ConsentRepository {

    private final List<Consent> consents = new ArrayList<>();

    @Override
    public void save(Consent consent) {
        consents.add(consent);
    }

    @Override
    public List<Consent> findByUserId(UserId userId) {
        return consents.stream()
                .filter(consent -> consent.userId().equals(userId))
                .toList();
    }

    List<Consent> all() {
        return List.copyOf(consents);
    }
}
