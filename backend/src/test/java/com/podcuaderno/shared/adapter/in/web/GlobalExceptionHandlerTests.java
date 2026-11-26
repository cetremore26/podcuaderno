package com.podcuaderno.shared.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;
import com.podcuaderno.shared.domain.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
@Import({SecurityConfiguration.class, GlobalExceptionHandlerTests.ThrowingController.class})
class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvcTester mvc;

    @Test
    @WithMockUser
    void resourceNotFoundIsMappedTo404WithoutExposingIdentifiers() {
        MvcTestResult result = mvc.get().uri("/test/not-found").exchange();

        assertProblem(result, HttpStatus.NOT_FOUND, "/errors/not-found");
        assertThat(result).bodyText().doesNotContain("secret-id-42");
    }

    @Test
    @WithMockUser
    void businessRuleViolationIsMappedTo422WithItsMessage() {
        MvcTestResult result = mvc.get().uri("/test/business-rule").exchange();

        assertProblem(result, HttpStatus.UNPROCESSABLE_CONTENT, "/errors/business-rule");
        assertThat(result).bodyJson().extractingPath("$.detail").isEqualTo("position is out of range");
    }

    @Test
    @WithMockUser
    void invalidRequestBodyIsMappedTo400() {
        MvcTestResult result = mvc.post()
                .uri("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}")
                .exchange();

        assertProblem(result, HttpStatus.BAD_REQUEST, "/errors/validation");
    }

    @Test
    @WithMockUser
    void unexpectedErrorIsMappedTo500WithCorrelationIdAndNoInternals() {
        MvcTestResult result = mvc.get().uri("/test/unexpected").exchange();

        assertProblem(result, HttpStatus.INTERNAL_SERVER_ERROR, "/errors/internal");
        assertThat(result).bodyJson().extractingPath("$.correlationId").isNotNull();
        assertThat(result).bodyText().doesNotContain("boom").doesNotContain("IllegalStateException");
    }

    @Test
    void requestWithoutUserIsMappedTo401() {
        MvcTestResult result = mvc.get().uri("/test/not-found").exchange();

        assertProblem(result, HttpStatus.UNAUTHORIZED, "/errors/unauthenticated");
    }

    private static void assertProblem(MvcTestResult result, HttpStatus status, String type) {
        assertThat(result).hasStatus(status).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.type").isEqualTo(type);
    }

    @RestController
    @RequestMapping("/test")
    static class ThrowingController {

        @GetMapping("/not-found")
        void notFound() {
            throw new SampleNotFoundException();
        }

        @GetMapping("/business-rule")
        void businessRule() {
            throw new SampleRuleViolationException();
        }

        @PostMapping("/validation")
        void validation(@Valid @RequestBody SampleRequest request) {
            // Never reached: validation fails before.
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("boom");
        }
    }

    record SampleRequest(@NotBlank String name) {}

    static class SampleNotFoundException extends ResourceNotFoundException {
        SampleNotFoundException() {
            super("sample secret-id-42 not found");
        }
    }

    static class SampleRuleViolationException extends BusinessRuleViolationException {
        SampleRuleViolationException() {
            super("position is out of range");
        }
    }
}
