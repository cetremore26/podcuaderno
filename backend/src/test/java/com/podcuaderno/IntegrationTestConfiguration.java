package com.podcuaderno;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/** Shared beans of {@link IntegrationTest}. */
@TestConfiguration(proxyBeanMethods = false)
@Import(TestcontainersConfiguration.class)
public class IntegrationTestConfiguration {

    @Bean
    @Primary
    RecordingEmailSender recordingEmailSender() {
        return new RecordingEmailSender();
    }
}
