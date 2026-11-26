package fixtures.architecture.sample.adapter.out.email;

import fixtures.architecture.sample.application.port.out.SampleNotifier;
import fixtures.architecture.sample.domain.model.Sample;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Outbound email adapter implementing an application port. */
@Component
public class SampleEmailNotifier implements SampleNotifier {

    private final List<String> sent = new ArrayList<>();

    @Override
    public void notifyCreated(Sample sample) {
        sent.add(sample.name());
    }
}
