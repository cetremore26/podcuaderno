package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.RefreshTokenFamilyId;
import com.podcuaderno.identity.domain.model.TokenHash;
import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {

    void save(RefreshToken token);

    Optional<RefreshToken> findByHash(TokenHash hash);

    /** Reads the token and locks its row until the transaction ends, so two renewals cannot both succeed. */
    Optional<RefreshToken> findByHashForUpdate(TokenHash hash);

    /** Revokes every token of the family that is not revoked yet. */
    void revokeFamily(RefreshTokenFamilyId familyId, Instant now);
}
