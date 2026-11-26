package fixtures.architecture.sample.adapter.out.persistence;

import fixtures.architecture.sample.domain.model.Sample;

final class SampleMapper {

    private SampleMapper() {}

    static SampleJpaEntity toEntity(Sample sample) {
        return new SampleJpaEntity(sample.id().value(), sample.name());
    }
}
