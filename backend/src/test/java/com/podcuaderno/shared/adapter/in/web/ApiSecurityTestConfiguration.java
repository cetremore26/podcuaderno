package com.podcuaderno.shared.adapter.in.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/** Lets {@code @WebMvcTest} slices of every module use the real API security (bearer tokens, CORS, 401 problems). */
@TestConfiguration(proxyBeanMethods = false)
@Import({SecurityConfiguration.class, JwtConfiguration.class})
public class ApiSecurityTestConfiguration {}
