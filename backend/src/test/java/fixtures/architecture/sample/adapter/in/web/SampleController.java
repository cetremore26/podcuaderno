package fixtures.architecture.sample.adapter.in.web;

import fixtures.architecture.sample.application.port.in.CreateSampleCommand;
import fixtures.architecture.sample.application.port.in.CreateSampleUseCase;
import fixtures.architecture.sample.domain.model.SampleId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    private final CreateSampleUseCase createSample;

    public SampleController(CreateSampleUseCase createSample) {
        this.createSample = createSample;
    }

    @PostMapping("/samples")
    public SampleId create(@RequestBody CreateSampleCommand command) {
        return createSample.create(command);
    }
}
