package fixtures.architecture.sample.adapter.out.persistence;

import fixtures.architecture.sample.application.port.out.SampleRepository;
import fixtures.architecture.sample.domain.model.Sample;
import org.springframework.stereotype.Component;

@Component
public class SamplePersistenceAdapter implements SampleRepository {

    private final SampleJpaRepository repository;

    public SamplePersistenceAdapter(SampleJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Sample sample) {
        repository.save(SampleMapper.toEntity(sample));
    }
}
