package fixtures.architecture.sample.application.port.out;

import fixtures.architecture.sample.domain.model.Sample;

public interface SampleRepository {

    void save(Sample sample);
}
