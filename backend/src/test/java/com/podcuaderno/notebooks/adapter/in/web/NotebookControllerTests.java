package com.podcuaderno.notebooks.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.podcuaderno.notebooks.application.port.in.CreateNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.CreateNotebookUseCase;
import com.podcuaderno.notebooks.application.port.in.DeleteNotebookUseCase;
import com.podcuaderno.notebooks.application.port.in.ListNotebooksUseCase;
import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookUseCase;
import com.podcuaderno.notebooks.domain.exception.InvalidTitleException;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.adapter.in.web.ApiSecurityTestConfiguration;
import com.podcuaderno.shared.adapter.in.web.TestAccessTokens;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(NotebookController.class)
@Import(ApiSecurityTestConfiguration.class)
class NotebookControllerTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final UserId USER = UserId.newId();

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockitoBean
    private CreateNotebookUseCase createNotebook;

    @MockitoBean
    private ListNotebooksUseCase listNotebooks;

    @MockitoBean
    private RenameNotebookUseCase renameNotebook;

    @MockitoBean
    private DeleteNotebookUseCase deleteNotebook;

    private String bearer() {
        return "Bearer " + TestAccessTokens.issue(jwtEncoder, USER);
    }

    private MvcTestResult create(String body) {
        return mvc.post()
                .uri("/api/v1/notebooks")
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }

    @Test
    void createAnswers201WithTheNewNotebook() {
        NotebookId id = NotebookId.newId();
        when(createNotebook.create(new CreateNotebookCommand(USER, "Cálculo")))
                .thenReturn(new NotebookSummary(id, "Cálculo", NOW));

        MvcTestResult result = create("{\"title\": \"Cálculo\"}");

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result)
                .bodyJson()
                .extractingPath("$.id")
                .isEqualTo(id.value().toString());
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Cálculo");
        assertThat(result).bodyJson().extractingPath("$.updatedAt").isEqualTo("2026-12-14T10:00:00Z");
    }

    @Test
    void createRejectsAnEmptyTitleWith400() {
        assertThat(create("{\"title\": \"\"}")).hasStatus(HttpStatus.BAD_REQUEST);
        verify(createNotebook, never()).create(any());
    }

    @Test
    void createReportsDomainTitleRulesWith422() {
        when(createNotebook.create(any())).thenThrow(new InvalidTitleException());

        assertThat(create("{\"title\": \"Título\"}"))
                .hasStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("invalid-title");
    }

    @Test
    void listReturnsTheRequestersNotebooks() {
        when(listNotebooks.list(USER)).thenReturn(List.of(new NotebookSummary(NotebookId.newId(), "Física", NOW)));

        assertThat(mvc.get().uri("/api/v1/notebooks").header(HttpHeaders.AUTHORIZATION, bearer()))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].title")
                .isEqualTo("Física");
    }

    @Test
    void everyEndpointRequiresAToken() {
        assertThat(mvc.get().uri("/api/v1/notebooks")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.delete().uri("/api/v1/notebooks/{id}", NotebookId.newId().value()))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void anInvalidNotebookIdAnswers400() {
        assertThat(mvc.patch()
                        .uri("/api/v1/notebooks/not-a-uuid")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Nuevo\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.type")
                .isEqualTo("/errors/validation");
    }
}
