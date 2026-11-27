package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.importing.domain.exception.DocumentTooLongException;
import com.podcuaderno.importing.domain.exception.EmptyDocumentException;
import com.podcuaderno.importing.domain.exception.TooManyConceptsException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class StructureDetectorTests {

    @Test
    void hashCreatesTopicsAndDoubleHashCreatesConcepts() {
        DetectedStructure structure = StructureDetector.detect(document(
                h1("Modelo relacional"),
                h2("Tablas"),
                p("Una tabla tiene filas."),
                h2("Llaves"),
                p("Identifican filas."),
                h1("Normalización"),
                h2("Primera forma normal"),
                p("Valores atómicos.")));

        assertThat(structure.source()).isEqualTo(StructureSource.HEADINGS);
        assertThat(structure.topics())
                .containsExactly(
                        new DetectedTopic(
                                "Modelo relacional",
                                List.of(
                                        new DetectedConcept("Tablas", "Una tabla tiene filas."),
                                        new DetectedConcept("Llaves", "Identifican filas."))),
                        new DetectedTopic(
                                "Normalización",
                                List.of(new DetectedConcept("Primera forma normal", "Valores atómicos."))));
        assertThat(structure.topicCount()).isEqualTo(2);
        assertThat(structure.conceptCount()).isEqualTo(3);
    }

    @Test
    void textBeforeTheFirstHeadingGoesToAnIntroductionTopic() {
        DetectedStructure structure =
                StructureDetector.detect(document(p("Notas del curso."), h1("Tema"), h2("Concepto"), p("Texto.")));

        assertThat(structure.topics().getFirst())
                .isEqualTo(new DetectedTopic(
                        "Introducción", List.of(new DetectedConcept("Introducción", "Notas del curso."))));
        assertThat(structure.topics().get(1).title()).isEqualTo("Tema");
    }

    @Test
    void onlyDoubleHashHeadingsMakeOneTopicNamedAfterTheDocument() {
        DetectedStructure structure = StructureDetector.detect(
                document(h2("Uno"), p("Primero."), h2("Dos"), p("Segundo.")), "apuntes_de-clase.md");

        assertThat(structure.suggestedTitle()).isEqualTo("Apuntes de clase");
        assertThat(structure.topics())
                .containsExactly(new DetectedTopic(
                        "Apuntes de clase",
                        List.of(new DetectedConcept("Uno", "Primero."), new DetectedConcept("Dos", "Segundo."))));
    }

    @Test
    void textUnderATopicBeforeItsFirstConceptBecomesAConceptNamedLikeTheTopic() {
        DetectedStructure structure =
                StructureDetector.detect(document(h1("Tema"), p("Resumen."), p("Más."), h2("Concepto"), p("Texto.")));

        assertThat(structure.topics().getFirst().concepts())
                .containsExactly(
                        new DetectedConcept("Tema", "Resumen.\n\nMás."), new DetectedConcept("Concepto", "Texto."));
    }

    @Test
    void deeperHeadingsStayAsTextOfTheCurrentConcept() {
        DetectedStructure structure = StructureDetector.detect(
                document(h1("Tema"), h2("Concepto"), p("Inicio."), new HeadingBlock(3, "Detalle"), p("Fin.")));

        assertThat(structure.topics().getFirst().concepts())
                .containsExactly(new DetectedConcept("Concepto", "Inicio.\n\nDetalle\n\nFin."));
    }

    @Test
    void withoutHeadingsEachParagraphIsAConceptTitledWithItsFirstSixWords() {
        DetectedStructure structure = StructureDetector.detect(document(
                p("La normalización es un proceso que reduce la redundancia."),
                p("Una tabla está en primera forma normal si sus valores son atómicos.")));

        assertThat(structure.source()).isEqualTo(StructureSource.PARAGRAPHS);
        assertThat(structure.suggestedTitle()).isEqualTo("Mis apuntes");
        assertThat(structure.topics()).hasSize(1);
        DetectedTopic topic = structure.topics().getFirst();
        assertThat(topic.title()).isEqualTo("Mis apuntes");
        assertThat(topic.concepts())
                .containsExactly(
                        new DetectedConcept(
                                "La normalización es un proceso que",
                                "La normalización es un proceso que reduce la redundancia."),
                        new DetectedConcept(
                                "Una tabla está en primera forma",
                                "Una tabla está en primera forma normal si sus valores son atómicos."));
    }

    @Test
    void deeperHeadingsWithoutTopicsOrConceptsAreParagraphs() {
        DetectedStructure structure =
                StructureDetector.detect(document(new HeadingBlock(3, "Detalle"), new HeadingBlock(4, ""), p("Texto")));

        assertThat(structure.source()).isEqualTo(StructureSource.PARAGRAPHS);
        assertThat(structure.topics().getFirst().concepts())
                .extracting(DetectedConcept::title)
                .containsExactly("Detalle", "Texto");
    }

    @Test
    void shortParagraphKeepsAllItsWordsAsTitle() {
        DetectedStructure structure = StructureDetector.detect(document(p("Dos palabras")));

        assertThat(structure.topics().getFirst().concepts().getFirst().title()).isEqualTo("Dos palabras");
    }

    @Test
    void trailingPunctuationLeavesTheTitle() {
        DetectedStructure structure = StructureDetector.detect(document(
                p("Hola, mundo."), p("Primero: segundo; tercero, cuarto. quinto sexto... séptimo"), p("¿Qué…")));

        assertThat(structure.topics().getFirst().concepts())
                .extracting(DetectedConcept::title)
                .containsExactly("Hola, mundo", "Primero: segundo; tercero, cuarto. quinto sexto", "¿Qué");
    }

    @Test
    void longHeadingIsCutAtAWordWithEllipsis() {
        String heading = "palabra ".repeat(30).strip();

        DetectedStructure structure = StructureDetector.detect(document(h1(heading), p("Texto")));

        String title = structure.topics().getFirst().title();
        assertThat(title).hasSizeLessThanOrEqualTo(150).endsWith("palabra…").startsWith("palabra palabra");
    }

    @Test
    void longWordIsCutWithoutSplittingASurrogatePair() {
        String heading = "a".repeat(148) + "\uD83D\uDCDA" + "b".repeat(10);

        String title = StructureDetector.detect(document(h1(heading), p("Texto")))
                .topics()
                .getFirst()
                .title();

        assertThat(title).isEqualTo("a".repeat(148) + "…");
    }

    @Test
    void longWordIsCutAtTheLimit() {
        String title = StructureDetector.detect(document(h1("x".repeat(300)), p("Texto")))
                .topics()
                .getFirst()
                .title();

        assertThat(title).isEqualTo("x".repeat(149) + "…");
    }

    @Test
    void emptyHeadingGetsAFallbackTitle() {
        DetectedStructure structure = StructureDetector.detect(document(h1(""), h2(" "), p("Texto")));

        assertThat(structure.topics().getFirst())
                .isEqualTo(new DetectedTopic(
                        "Tema sin título", List.of(new DetectedConcept("Concepto sin título", "Texto"))));
    }

    @Test
    void emptyConceptsAndTopicsAreKept() {
        DetectedStructure structure = StructureDetector.detect(document(h1("Uno"), h2("Vacío"), h1("Dos")));

        assertThat(structure.topics())
                .containsExactly(
                        new DetectedTopic("Uno", List.of(new DetectedConcept("Vacío", ""))),
                        new DetectedTopic("Dos", List.of()));
        assertThat(structure.characterCount()).isZero();
    }

    @Test
    void duplicateTitlesAreKept() {
        DetectedStructure structure =
                StructureDetector.detect(document(h1("Tema"), h2("Igual"), p("a"), h2("Igual"), p("b"), h1("Tema")));

        assertThat(structure.topics()).extracting(DetectedTopic::title).containsExactly("Tema", "Tema");
        assertThat(structure.topics().getFirst().concepts())
                .extracting(DetectedConcept::title)
                .containsExactly("Igual", "Igual");
    }

    @Test
    void emptyDocumentIsRejected() {
        assertThatThrownBy(() -> StructureDetector.detect(document()))
                .isInstanceOf(EmptyDocumentException.class)
                .hasFieldOrPropertyWithValue("code", "empty-document");
        assertThatThrownBy(() -> StructureDetector.detect(document(h1(""), h2(""), p("  "))))
                .isInstanceOf(EmptyDocumentException.class);
    }

    @Test
    void moreThan200000CharactersIsRejected() {
        DetectedStructure atTheLimit = StructureDetector.detect(document(p("a".repeat(200_000))));
        assertThat(atTheLimit.characterCount()).isEqualTo(200_000);

        assertThatThrownBy(() -> StructureDetector.detect(document(p("a".repeat(200_001)))))
                .isInstanceOf(DocumentTooLongException.class)
                .hasFieldOrPropertyWithValue("code", "text-too-long");
    }

    @Test
    void moreThan5000ConceptsIsRejected() {
        assertThat(StructureDetector.detect(paragraphs(5_000)).conceptCount()).isEqualTo(5_000);

        assertThatThrownBy(() -> StructureDetector.detect(paragraphs(5_001)))
                .isInstanceOf(TooManyConceptsException.class)
                .hasFieldOrPropertyWithValue("code", "too-many-concepts");
    }

    @Test
    void moreThan500TopicsIsRejected() {
        List<DocumentBlock> blocks = new ArrayList<>();
        IntStream.range(0, 501).forEach(index -> blocks.add(h1("Tema " + index)));

        assertThatThrownBy(() -> StructureDetector.detect(new RawDocument(blocks, List.of())))
                .isInstanceOf(TooManyConceptsException.class);
    }

    @Test
    void suggestedTitleComesFromTheFileNameThenTheFirstTopic() {
        RawDocument withTopics = document(h2("Antes"), h1(""), h1("Bases de datos"), p("Texto"));

        assertThat(StructureDetector.detect(withTopics, "C:\\apuntes\\clase-3.md")
                        .suggestedTitle())
                .isEqualTo("Clase 3");
        assertThat(StructureDetector.detect(withTopics, "carpeta/ñandú_notas.txt")
                        .suggestedTitle())
                .isEqualTo("Ñandú notas");
        assertThat(StructureDetector.detect(withTopics, ".md").suggestedTitle()).isEqualTo("Bases de datos");
        assertThat(StructureDetector.detect(withTopics).suggestedTitle()).isEqualTo("Bases de datos");
        assertThat(StructureDetector.detect(document(h1(""), p("Texto"))).suggestedTitle())
                .isEqualTo("Mis apuntes");
    }

    @Test
    void omittedElementsAreReported() {
        List<OmittedElement> omitted = List.of(
                new OmittedElement(OmittedElementKind.CODE_BLOCK, 2), new OmittedElement(OmittedElementKind.TABLE, 1));

        DetectedStructure structure = StructureDetector.detect(new RawDocument(List.of(p("Texto")), omitted));

        assertThat(structure.omitted()).isEqualTo(omitted);
    }

    private static RawDocument document(DocumentBlock... blocks) {
        return new RawDocument(List.of(blocks), List.of());
    }

    private static RawDocument paragraphs(int count) {
        return new RawDocument(
                IntStream.range(0, count)
                        .<DocumentBlock>mapToObj(index -> p("Párrafo " + index))
                        .toList(),
                List.of());
    }

    private static HeadingBlock h1(String text) {
        return new HeadingBlock(1, text);
    }

    private static HeadingBlock h2(String text) {
        return new HeadingBlock(2, text);
    }

    private static ParagraphBlock p(String text) {
        return new ParagraphBlock(text);
    }
}
