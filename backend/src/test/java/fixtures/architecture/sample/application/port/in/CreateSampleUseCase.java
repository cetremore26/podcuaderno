package fixtures.architecture.sample.application.port.in;

import fixtures.architecture.sample.domain.model.SampleId;

public interface CreateSampleUseCase {

    SampleId create(CreateSampleCommand command);
}
