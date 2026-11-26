package com.podcuaderno.architecture;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Proves that the architecture rules accept the reference structure and catch violations (ADR-0008). */
class ArchitectureRulesSelfTests {

    private static final JavaClasses SAMPLE = new ClassFileImporter().importPackages("fixtures.architecture.sample");
    private static final JavaClasses VIOLATIONS =
            new ClassFileImporter().importPackages("fixtures.architecture.violations");

    static Stream<Arguments> allRules() {
        return Stream.of(DependencyRules.class, NamingRules.class)
                .flatMap(rules -> Arrays.stream(rules.getDeclaredFields()))
                .filter(field -> ArchRule.class.isAssignableFrom(field.getType()))
                .map(field -> Arguments.of(field.getName(), readRule(field)));
    }

    private static ArchRule readRule(Field field) {
        try {
            field.setAccessible(true);
            return (ArchRule) field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allRules")
    void compliantModulePassesEveryRule(String name, ArchRule rule) {
        assertThatCode(() -> rule.check(SAMPLE)).doesNotThrowAnyException();
    }

    @Test
    void domainIsFrameworkFreeDetectsSpringInTheDomain() {
        assertViolation(DependencyRules.domainIsFrameworkFree, "FrameworkCoupledModel");
    }

    @Test
    void cleanLayersDetectsDomainDependingOnAnAdapter() {
        assertViolation(DependencyRules.cleanLayers, "LeakyModel");
    }

    @Test
    void noFieldInjectionDetectsAutowiredFields() {
        assertViolation(DependencyRules.noFieldInjection, "FieldInjectedController");
    }

    @Test
    void applicationServicesImplementUseCasesDetectsBadlyNamedClasses() {
        assertViolation(NamingRules.applicationServicesImplementUseCases, "NotifyUsersHelper");
    }

    private static void assertViolation(ArchRule rule, String offendingClass) {
        assertThatThrownBy(() -> rule.check(VIOLATIONS))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(offendingClass);
    }
}
