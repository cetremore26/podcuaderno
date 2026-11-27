package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentFormatTests {

    @ParameterizedTest
    @CsvSource({
        "notas.txt, PLAIN_TEXT",
        "NOTAS.TXT, PLAIN_TEXT",
        "notas.md, MARKDOWN",
        "Notas.Md, MARKDOWN",
        "notas.markdown, MARKDOWN",
        "notas.MARKDOWN, MARKDOWN"
    })
    void theExtensionGivesTheFormat(String fileName, DocumentFormat format) {
        assertThat(DocumentFormat.fromFileName(fileName)).contains(format);
    }

    @ParameterizedTest
    @ValueSource(strings = {"notas.pdf", "notas.docx", "notas", "", "md", "notas.txt.exe"})
    void otherExtensionsAreNotSupported(String fileName) {
        assertThat(DocumentFormat.fromFileName(fileName)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "# Tema",
                "## Concepto",
                "   ## Concepto",
                "texto\n# Tema",
                "texto\r\n## Concepto\r\n",
                "#",
                "##\r",
                "uno\n  #"
            })
    void pastedTextWithTopicOrConceptHeadingsIsMarkdown(String text) {
        assertThat(DocumentFormat.sniff(text)).isEqualTo(DocumentFormat.MARKDOWN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"#tema", "#### Detalle", "    # Código", "2*3*4 = 24", "", "### Sub", "texto # no"})
    void pastedTextWithoutThoseHeadingsIsPlainText(String text) {
        assertThat(DocumentFormat.sniff(text)).isEqualTo(DocumentFormat.PLAIN_TEXT);
    }
}
