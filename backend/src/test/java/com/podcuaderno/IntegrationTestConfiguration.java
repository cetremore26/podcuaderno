package com.podcuaderno;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/** Shared beans of {@link IntegrationTest}. */
@TestConfiguration(proxyBeanMethods = false)
@Import(TestcontainersConfiguration.class)
public class IntegrationTestConfiguration {}
