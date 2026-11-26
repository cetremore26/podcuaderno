package com.podcuaderno.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.podcuaderno.shared.domain.exception.DomainException;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

/** Verified naming conventions (docs/05 §11). */
final class NamingRules {

    private static final String PACKAGE_INFO = "package-info";
    private static final String PORT_IN = "..application.port.in..";
    private static final String APPLICATION_SERVICE = "..application.service..";
    private static final String PERSISTENCE_ADAPTER = "..adapter.out.persistence..";

    @ArchTest
    static final ArchRule useCasesAreInboundPortInterfaces = classes()
            .that()
            .haveSimpleNameEndingWith("UseCase")
            .should()
            .beInterfaces()
            .andShould()
            .resideInAPackage(PORT_IN)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule inboundPortInterfacesAreUseCases = classes()
            .that()
            .resideInAPackage(PORT_IN)
            .and()
            .areInterfaces()
            .and()
            .doNotHaveSimpleName(PACKAGE_INFO)
            .should()
            .haveSimpleNameEndingWith("UseCase")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule commandsAreInboundPortRecords = classes()
            .that()
            .haveSimpleNameEndingWith("Command")
            .should()
            .beAssignableTo(Record.class)
            .andShould()
            .resideInAPackage(PORT_IN)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule applicationServicesImplementUseCases = classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .areTopLevelClasses()
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Service")
            .andShould()
            .implement(JavaClass.Predicates.simpleNameEndingWith("UseCase"))
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule servicesLiveInApplicationService = classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .should()
            .resideInAPackage(APPLICATION_SERVICE)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositoryPortsAreOutboundInterfaces = classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .and()
            .haveSimpleNameNotEndingWith("JpaRepository")
            .should()
            .beInterfaces()
            .andShould()
            .resideInAPackage("..application.port.out..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllersAreWebAdapters = classes()
            .that()
            .areAnnotatedWith(RestController.class)
            .should()
            .haveSimpleNameEndingWith("Controller")
            .andShould()
            .resideInAPackage("..adapter.in.web..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllerSuffixIsReserved = classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(RestController.class)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule eventListenersAreEventAdapters = classes()
            .that()
            .resideInAPackage("..adapter.in.event..")
            .and()
            .areTopLevelClasses()
            .and()
            .doNotHaveSimpleName(PACKAGE_INFO)
            .should()
            .haveSimpleNameEndingWith("Listener")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule jpaEntitiesArePersistenceAdapters = classes()
            .that()
            .areAnnotatedWith("jakarta.persistence.Entity")
            .should()
            .haveSimpleNameEndingWith("JpaEntity")
            .andShould()
            .resideInAPackage(PERSISTENCE_ADAPTER)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule springDataRepositoriesArePersistenceAdapters = classes()
            .that()
            .areInterfaces()
            .and()
            .areAssignableTo("org.springframework.data.repository.Repository")
            .should()
            .haveSimpleNameEndingWith("JpaRepository")
            .andShould()
            .resideInAPackage(PERSISTENCE_ADAPTER)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule mappersAreAdapters = classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .should()
            .resideInAPackage("..adapter..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule exceptionsAreUncheckedAndSuffixed = classes()
            .that()
            .areAssignableTo(Throwable.class)
            .should()
            .haveSimpleNameEndingWith("Exception")
            .andShould()
            .beAssignableTo(RuntimeException.class)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule exceptionSuffixIsReserved = classes()
            .that()
            .haveSimpleNameEndingWith("Exception")
            .should()
            .beAssignableTo(RuntimeException.class)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainExceptionsExtendDomainException = classes()
            .that()
            .resideInAPackage("..domain..")
            .and()
            .areAssignableTo(Throwable.class)
            .should()
            .beAssignableTo(DomainException.class)
            .andShould()
            .resideInAPackage("..domain.exception..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainEventsAreRecords = classes()
            .that()
            .resideInAPackage("..domain.event..")
            .and()
            .doNotHaveSimpleName(PACKAGE_INFO)
            .should()
            .beAssignableTo(Record.class)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule identifiersAreRecords = classes()
            .that()
            .resideInAPackage("..domain..")
            .and()
            .haveSimpleNameEndingWith("Id")
            .should()
            .beAssignableTo(Record.class)
            .allowEmptyShould(true);

    private NamingRules() {}
}
