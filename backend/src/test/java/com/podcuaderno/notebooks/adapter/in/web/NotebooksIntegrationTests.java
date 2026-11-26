package com.podcuaderno.notebooks.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import com.podcuaderno.IntegrationTest;
import com.podcuaderno.shared.adapter.in.web.TestAccessTokens;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class NotebooksIntegrationTests {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    private final UserId student = UserId.newId();

    private String token() {
        return "Bearer " + TestAccessTokens.issue(jwtEncoder, student);
    }

    private UUID create(String title) throws Exception {
        MvcTestResult created = mvc.post()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.AUTHORIZATION, token())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"%s\"}".formatted(title))
                .exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED);
        return UUID.fromString(JsonPath.read(created.getResponse().getContentAsString(), "$.id"));
    }

    private List<String> titles() throws Exception {
        MvcTestResult list = mvc.get()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.AUTHORIZATION, token())
                .exchange();
        assertThat(list).hasStatusOk();
        return JsonPath.read(list.getResponse().getContentAsString(), "$[*].title");
    }

    @Test
    void renamingANotebookMovesItToTheTopOfTheList() throws Exception {
        UUID first = create("Bases de datos");
        create("Cálculo integral");
        assertThat(titles()).containsExactly("Cálculo integral", "Bases de datos");

        assertThat(mvc.patch()
                        .uri("/api/v1/notebooks/{id}", first)
                        .header(HttpHeaders.AUTHORIZATION, token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Bases de datos II\"}"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.title")
                .isEqualTo("Bases de datos II");

        assertThat(titles()).containsExactly("Bases de datos II", "Cálculo integral");
    }

    @Test
    void deletingANotebookRemovesItFromTheList() throws Exception {
        UUID id = create("Física");

        assertThat(mvc.delete().uri("/api/v1/notebooks/{id}", id).header(HttpHeaders.AUTHORIZATION, token()))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(titles()).isEmpty();
    }

    @Test
    void theOpenApiDocumentDescribesTheNotebookEndpoints() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .hasStatusOk()
                .bodyText()
                .contains("/api/v1/notebooks", "/api/v1/notebooks/{notebookId}")
                .doesNotContain("AuthenticatedUser");
    }
}
