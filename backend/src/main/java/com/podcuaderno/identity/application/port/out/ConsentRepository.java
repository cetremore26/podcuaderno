package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;

public interface ConsentRepository {

    void save(Consent consent);

    List<Consent> findByUserId(UserId userId);
}
