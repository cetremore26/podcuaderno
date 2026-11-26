package com.podcuaderno.identity.adapter.out.persistence;

import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.Role;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.identity.domain.model.UserStatus;
import com.podcuaderno.shared.domain.UserId;

final class UserMapper {

    private UserMapper() {}

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.email().value(),
                user.passwordHash().value(),
                user.status().name(),
                user.role().name(),
                user.createdAt(),
                user.emailVerifiedAt().orElse(null));
    }

    static User toDomain(UserJpaEntity entity) {
        return User.restore(
                new UserId(entity.getId()),
                new EmailAddress(entity.getEmail()),
                new PasswordHash(entity.getPasswordHash()),
                UserStatus.valueOf(entity.getStatus()),
                Role.valueOf(entity.getRole()),
                entity.getCreatedAt(),
                entity.getEmailVerifiedAt());
    }
}
