package fixtures.architecture.sample.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class SampleJpaEntity {

    @Id
    private UUID id;

    private String name;

    protected SampleJpaEntity() {}

    SampleJpaEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
