package com.podcuaderno.metrics.application.port.out;

import com.podcuaderno.metrics.domain.model.UsageEvent;

public interface UsageEventRepository {

    void save(UsageEvent event);
}
