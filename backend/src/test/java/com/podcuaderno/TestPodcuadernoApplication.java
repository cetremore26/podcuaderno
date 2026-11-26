package com.podcuaderno;

import org.springframework.boot.SpringApplication;

/** Runs the application locally against a PostgreSQL container ({@code ./gradlew bootTestRun}). */
public class TestPodcuadernoApplication {

    public static void main(String[] args) {
        SpringApplication.from(PodcuadernoApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
