package fixtures.architecture.sample.domain.model;

/** Aggregate root of the compliant fixture module. */
public class Sample {

    private final SampleId id;
    private String name;

    public Sample(SampleId id, String name) {
        this.id = id;
        this.name = name;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public SampleId id() {
        return id;
    }

    public String name() {
        return name;
    }
}
