package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.RefreshToken;

public interface RefreshTokenRepository {

    void save(RefreshToken token);
}
