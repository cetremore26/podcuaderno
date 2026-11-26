package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    UserPersistenceAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        jpa.save(UserMapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpa.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return jpa.findByEmail(email.value()).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return jpa.existsByEmail(email.value());
    }
}
