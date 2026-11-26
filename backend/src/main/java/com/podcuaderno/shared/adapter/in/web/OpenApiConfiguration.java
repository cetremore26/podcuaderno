package com.podcuaderno.shared.adapter.in.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Describes the bearer access token in the OpenAPI document (ADR-0009). */
@Configuration(proxyBeanMethods = false)
class OpenApiConfiguration {

    static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    OpenAPI podcuadernoOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Podcuaderno API").version("v1"))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
