package com.podcuaderno.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.transaction.annotation.Transactional;

/** Dependency rules of Clean Architecture inside every module (docs/05 §5.3 and §11, ADR-0002, ADR-0008). */
final class DependencyRules {

    private static final String APPLICATION_SERVICE = "..application.service..";
    private static final String JAKARTA_TRANSACTIONAL = "jakarta.transaction.Transactional";

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .and()
            .doNotHaveSimpleName("package-info")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..",
                    "jakarta.validation..",
                    "org.hibernate..",
                    "com.fasterxml..",
                    "tools.jackson..")
            .allowEmptyShould(true);

    /** Every outbound adapter package must be declared here, or the application layer rejects it (ADR-0010). */
    @ArchTest
    static final ArchRule cleanLayers = onionArchitecture()
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("web", "..adapter.in.web..")
            .adapter("events", "..adapter.in.event..")
            .adapter("persistence", "..adapter.out.persistence..")
            .adapter("eventPublishing", "..adapter.out.event..")
            .adapter("email", "..adapter.out.email..")
            .adapter("security", "..adapter.out.security..")
            .withOptionalLayers(true)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule coreDoesNotDependOnAdapters = noClasses()
            .that()
            .resideInAnyPackage("..domain..", "..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..adapter..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule applicationIsDeliveryAgnostic = noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework.web..",
                    "org.springframework.http..",
                    "jakarta.servlet..",
                    "org.springframework.security..",
                    "jakarta.persistence..",
                    "org.springframework.data..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule transactionsOnlyInApplicationServices = noClasses()
            .that()
            .resideOutsideOfPackage(APPLICATION_SERVICE)
            .should()
            .beAnnotatedWith(Transactional.class)
            .orShould()
            .beAnnotatedWith(JAKARTA_TRANSACTIONAL)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule transactionalMethodsOnlyInApplicationServices = noMethods()
            .that()
            .areDeclaredInClassesThat()
            .resideOutsideOfPackage(APPLICATION_SERVICE)
            .should()
            .beAnnotatedWith(Transactional.class)
            .orShould()
            .beAnnotatedWith(JAKARTA_TRANSACTIONAL)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule sharedKernelIsIndependent = noClasses()
            .that()
            .resideInAPackage("com.podcuaderno.shared..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "com.podcuaderno.identity..",
                    "com.podcuaderno.notebooks..",
                    "com.podcuaderno.importing..",
                    "com.podcuaderno.metrics..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule noFieldInjection = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule noStandardStreams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule noJavaUtilLogging = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    private DependencyRules() {}
}
