package fixtures.architecture.violations.domain;

import fixtures.architecture.violations.adapter.out.persistence.LeakyPersistenceAdapter;

/** Violates cleanLayers: the domain depends on an adapter. */
public class LeakyModel {

    private LeakyPersistenceAdapter adapter;
}
