package com.podcuaderno.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.PodcuadernoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/** Module boundaries verified by Spring Modulith (ADR-0004, ADR-0008). */
class ModularityTests {

    private static final ApplicationModules MODULES = ApplicationModules.of(PodcuadernoApplication.class);

    @Test
    void modulesRespectTheirBoundaries() {
        MODULES.verify();
    }

    @Test
    void sharedKernelIsDetectedAsModule() {
        assertThat(MODULES.getModuleByName("shared")).isPresent();
    }

    @Test
    void writesModuleDocumentation() {
        // Output goes to build/spring-modulith-docs.
        new Documenter(MODULES).writeDocumentation();
    }
}
