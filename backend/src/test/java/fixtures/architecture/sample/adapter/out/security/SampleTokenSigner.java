package fixtures.architecture.sample.adapter.out.security;

import fixtures.architecture.sample.domain.model.SampleId;
import org.springframework.stereotype.Component;

/** Outbound security adapter depending on the domain. */
@Component
public class SampleTokenSigner {

    public String sign(SampleId id) {
        return "signed:" + id.value();
    }
}
