package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** In-memory fake of {@link UserRepository} for service tests. */
class InMemoryUsers implements UserRepository {

    private final Map<UserId, User> users = new LinkedHashMap<>();
    private int saves;

    @Override
    public void save(User user) {
        saves++;
        users.put(user.id(), user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return users.values().stream().filter(user -> user.email().equals(email)).findFirst();
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return findByEmail(email).isPresent();
    }

    int count() {
        return users.size();
    }

    int saves() {
        return saves;
    }
}
