package fixtures.architecture.sample.application.service;

import fixtures.architecture.sample.application.port.in.CreateSampleCommand;
import fixtures.architecture.sample.application.port.in.CreateSampleUseCase;
import fixtures.architecture.sample.application.port.out.SampleRepository;
import fixtures.architecture.sample.domain.model.Sample;
import fixtures.architecture.sample.domain.model.SampleId;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSampleService implements CreateSampleUseCase {

    private final SampleRepository repository;

    public CreateSampleService(SampleRepository repository) {
        this.repository = repository;
    }

    @Override
    public SampleId create(CreateSampleCommand command) {
        Sample sample = new Sample(new SampleId(UUID.randomUUID()), command.name());
        repository.save(sample);
        return sample.id();
    }
}
