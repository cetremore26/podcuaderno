package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}
