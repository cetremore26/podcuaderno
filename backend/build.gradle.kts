plugins {
    java
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
}

group = "com.podcuaderno"
version = "0.0.1"
description = "Podcuaderno API"

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

repositories { mavenCentral() }

// Reproducible builds: resolved versions are pinned in gradle.lockfile.
// Refresh after changing dependencies with: ./gradlew dependencies --write-locks
dependencyLocking { lockAllConfigurations() }

dependencyManagement {
    imports { mavenBom("org.springframework.modulith:spring-modulith-bom:${libs.versions.spring.modulith.get()}") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    implementation(libs.springdoc.webmvc.ui)
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(libs.archunit.junit5)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot { buildInfo { excludes = setOf("time") } } // exposes version in /actuator/info

tasks.bootJar { archiveFileName = "podcuaderno.jar" }
tasks.jar { enabled = false }

tasks.withType<Test>().configureEach { useJUnitPlatform() }

spotless {
    java {
        target("src/**/*.java")
        palantirJavaFormat(libs.versions.palantir.java.format.get())
        formatAnnotations()
    }
}

jacoco { toolVersion = libs.versions.jacoco.get() }

// Coverage gate applies to business code only (docs/05 §10, RNF-08).
val coverageScope = listOf("com/podcuaderno/**/domain/**", "com/podcuaderno/**/application/**")

tasks.test { finalizedBy(tasks.jacocoTestReport) }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    classDirectories.setFrom(sourceSets.main.get().output.classesDirs.asFileTree.matching { include(coverageScope) })
    onlyIf("there are classes in the coverage scope") { !classDirectories.asFileTree.isEmpty }
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }
