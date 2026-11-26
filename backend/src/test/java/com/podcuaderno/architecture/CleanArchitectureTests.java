package com.podcuaderno.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

/** Clean Architecture and naming rules applied to the production code (ADR-0004). */
@AnalyzeClasses(packages = "com.podcuaderno", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTests {

    @ArchTest
    static final ArchTests dependencyRules = ArchTests.in(DependencyRules.class);

    @ArchTest
    static final ArchTests namingRules = ArchTests.in(NamingRules.class);
}
