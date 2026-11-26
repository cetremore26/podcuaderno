package com.podcuaderno.notebooks.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import com.podcuaderno.IntegrationTest;
import com.podcuaderno.shared.adapter.in.web.TestAccessTokens;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/** Owner-only access on every endpoint that receives a notebook id (HU-06, R-06). */
@IntegrationTest
class NotebookOwnershipIntegrationTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    static Stream<Arguments> endpointsWithNotebookId() {
        return Stream.of(
                Arguments.of("PATCH", "/api/v1/notebooks/{notebookId}", "{\"title\":\"Ajeno\"}"),
                Arguments.of("DELETE", "/api/v1/notebooks/{notebookId}", null));
    }

    private String tokenOf(UserId user) {
        return "Bearer " + TestAccessTokens.issue(jwtEncoder, user);
    }

    private UUID createNotebookAs(UserId owner, String title) throws Exception {
        MvcTestResult created = mvc.post()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.AUTHORIZATION, tokenOf(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"%s\"}".formatted(title))
                .exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED);
        return UUID.fromString(JsonPath.read(created.getResponse().getContentAsString(), "$.id"));
    }

    private List<String> titlesOf(UserId owner) throws Exception {
        MvcTestResult list = mvc.get()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.AUTHORIZATION, tokenOf(owner))
                .exchange();
        return JsonPath.read(list.getResponse().getContentAsString(), "$[*].title");
    }

    private MvcTestResult call(String method, String path, UUID notebookId, @Nullable String body, UserId as) {
        var request = mvc.method(HttpMethod.valueOf(method))
                .uri(path, notebookId)
                .header(HttpHeaders.AUTHORIZATION, tokenOf(as));
        if (body != null) {
            request = request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return request.exchange();
    }

    /** The problem without {@code instance}, which only echoes the requested path. */
    private static Map<String, Object> problemOf(MvcTestResult result) throws Exception {
        Map<String, Object> problem = JsonPath.read(result.getResponse().getContentAsString(), "$");
        problem.remove("instance");
        return problem;
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("endpointsWithNotebookId")
    void anotherUsersNotebookAnswersExactlyLikeAMissingOne(String method, String path, @Nullable String body)
            throws Exception {
        UserId owner = UserId.newId();
        UserId intruder = UserId.newId();
        UUID notebookId = createNotebookAs(owner, "Cálculo");

        MvcTestResult foreign = call(method, path, notebookId, body, intruder);
        MvcTestResult missing = call(method, path, UUID.randomUUID(), body, intruder);

        assertThat(foreign)
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.type")
                .isEqualTo("/errors/not-found");
        assertThat(missing).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(problemOf(foreign)).isEqualTo(problemOf(missing));
        assertThat(foreign.getResponse().getContentAsString()).doesNotContain("Cálculo");
        assertThat(titlesOf(owner)).containsExactly("Cálculo"); // untouched
    }

    @Test
    void listShowsOnlyTheRequestersNotebooks() throws Exception {
        UserId ana = UserId.newId();
        UserId luis = UserId.newId();
        createNotebookAs(ana, "Química");
        createNotebookAs(luis, "Historia");

        assertThat(titlesOf(ana)).containsExactly("Química");
        assertThat(titlesOf(luis)).containsExactly("Historia");
    }

    @Test
    void everyEndpointWithANotebookIdIsCoveredHere(
            @Autowired @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        Set<String> declared = mapping.getHandlerMethods().keySet().stream()
                .filter(info -> info.getPathPatternsCondition() != null)
                .flatMap(info -> info.getPathPatternsCondition().getPatternValues().stream()
                        .filter(pattern -> pattern.contains("{notebookId}"))
                        .flatMap(pattern -> info.getMethodsCondition().getMethods().stream()
                                .map(httpMethod -> httpMethod.name() + " " + pattern)))
                .collect(Collectors.toSet());
        Set<String> covered = endpointsWithNotebookId()
                .map(arguments -> arguments.get()[0] + " " + arguments.get()[1])
                .collect(Collectors.toSet());

        assertThat(declared).isNotEmpty();
        assertThat(covered).containsAll(declared); // a new endpoint without an ownership test fails here (R-06)
    }
}
