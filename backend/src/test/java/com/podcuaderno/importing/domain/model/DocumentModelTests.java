package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentModelTests {

    @Test
    void blocksCleanTheirText() {
        assertThat(new HeadingBlock(2, "  Tema" + Character.toString(0x200B) + "  uno ").text())
                .isEqualTo("Tema uno");
        assertThat(new ParagraphBlock("uno \r\n dos").text()).isEqualTo("uno\ndos");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7})
    void headingLevelsGoFromOneToSix(int level) {
        assertThatThrownBy(() -> new HeadingBlock(level, "Tema")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rawDocumentDropsEmptyParagraphsAndCopiesItsLists() {
        List<DocumentBlock> blocks = new ArrayList<>(List.of(new ParagraphBlock(" "), new HeadingBlock(3, "Sub")));

        RawDocument document = new RawDocument(blocks, List.of());
        blocks.clear();

        assertThat(document.blocks()).containsExactly(new HeadingBlock(3, "Sub"));
        assertThat(document.isEmpty()).isFalse();
        assertThat(document.hasStructuralHeadings()).isFalse();
        assertThat(document.hasTopicHeadings()).isFalse();
    }

    @Test
    void rawDocumentKnowsWhichHeadingsItHas() {
        RawDocument conceptsOnly = new RawDocument(List.of(new HeadingBlock(2, "Concepto")), List.of());
        RawDocument withTopics = new RawDocument(List.of(new HeadingBlock(1, "Tema")), List.of());

        assertThat(conceptsOnly.hasStructuralHeadings()).isTrue();
        assertThat(conceptsOnly.hasTopicHeadings()).isFalse();
        assertThat(withTopics.hasTopicHeadings()).isTrue();
    }

    @Test
    void omittedElementsCountAtLeastOne() {
        assertThatThrownBy(() -> new OmittedElement(OmittedElementKind.IMAGE, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(new OmittedElement(OmittedElementKind.HTML, 3).count()).isEqualTo(3);
    }

    @Test
    void detectedStructureCountsTopicsConceptsAndCharacters() {
        DetectedStructure structure = new DetectedStructure(
                "Título",
                StructureSource.PARAGRAPHS,
                List.of(
                        new DetectedTopic(
                                "Uno", List.of(new DetectedConcept("a", "abc"), new DetectedConcept("b", "de"))),
                        new DetectedTopic("Dos", List.of())),
                List.of());

        assertThat(structure.topicCount()).isEqualTo(2);
        assertThat(structure.conceptCount()).isEqualTo(2);
        assertThat(structure.characterCount()).isEqualTo(5);
    }
}
