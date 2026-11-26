package com.podcuaderno.shared.adapter.in.web;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;
import com.podcuaderno.shared.domain.exception.ResourceNotFoundException;
import java.net.URI;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Translates every error into a Problem Details response (RFC 9457, docs/05 §5.6). Never exposes stack traces or
 * internal messages to the client.
 */
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        // Fixed detail: the message could reveal identifiers of resources owned by other users.
        return problem(
                HttpStatus.NOT_FOUND,
                ProblemTypes.NOT_FOUND,
                "Resource not found",
                "The requested resource does not exist.");
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        return problem(
                HttpStatus.UNPROCESSABLE_CONTENT,
                ProblemTypes.BUSINESS_RULE,
                "Business rule violated",
                ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handleAuthentication(AuthenticationException ex) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                ProblemTypes.UNAUTHENTICATED,
                "Authentication required",
                "Full authentication is required to access this resource.");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Unexpected error [correlationId={}]", correlationId, ex);
        ProblemDetail problem = problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemTypes.INTERNAL,
                "Unexpected error",
                "An unexpected error occurred. Quote the correlation id when reporting it.");
        problem.setProperty("correlationId", correlationId);
        return problem;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        // Spring MVC exceptions usually arrive with a null body; super builds the ProblemDetail from the exception.
        ResponseEntity<Object> response = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (response != null && response.getBody() instanceof ProblemDetail problem) {
            if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
                problem.setType(ProblemTypes.VALIDATION);
            } else if (statusCode.value() == HttpStatus.NOT_FOUND.value()) {
                problem.setType(ProblemTypes.NOT_FOUND);
            }
        }
        return response;
    }

    private static ProblemDetail problem(HttpStatusCode status, URI type, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(type);
        problem.setTitle(title);
        return problem;
    }
}
