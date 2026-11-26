package fixtures.architecture.violations.domain;

import org.springframework.stereotype.Component;

/** Violates domainIsFrameworkFree: the domain depends on Spring. */
@Component
public class FrameworkCoupledModel {}
