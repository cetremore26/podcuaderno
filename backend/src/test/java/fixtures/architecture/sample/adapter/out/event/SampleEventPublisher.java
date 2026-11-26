package fixtures.architecture.sample.adapter.out.event;

import fixtures.architecture.sample.domain.event.SampleCreated;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** Outbound event adapter delegating to Spring. */
@Component
public class SampleEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SampleEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(SampleCreated event) {
        publisher.publishEvent(event);
    }
}
