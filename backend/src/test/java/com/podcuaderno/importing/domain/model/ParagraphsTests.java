package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ParagraphsTests {

    @Test
    void blankLinesSeparateParagraphsAndInnerLinesStayTogether() {
        assertThat(Paragraphs.split("uno\ndos\n\ntres\n\n\ncuatro\n")).containsExactly("uno\ndos", "tres", "cuatro");
    }

    @Test
    void withoutBlankLinesEachLineIsAParagraph() {
        assertThat(Paragraphs.split("uno\r\ndos\rtres\n")).containsExactly("uno", "dos", "tres");
    }

    @Test
    void linesWithOnlySpacesAreBlank() {
        assertThat(Paragraphs.split("uno\n   \ndos\n\t\ntres")).containsExactly("uno", "dos", "tres");
    }

    @Test
    void blankTextHasNoParagraphs() {
        assertThat(Paragraphs.split("  \n \n")).isEmpty();
    }
}
