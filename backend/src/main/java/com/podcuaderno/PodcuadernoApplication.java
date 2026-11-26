package com.podcuaderno;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point of the Podcuaderno modular monolith. Hosts the application-wide infrastructure: asynchronous event
 * listeners and the {@link Clock} injected by the services (ADR-0010).
 */
@SpringBootApplication
@EnableAsync
public class PodcuadernoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PodcuadernoApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
