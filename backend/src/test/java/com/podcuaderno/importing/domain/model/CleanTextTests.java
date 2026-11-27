package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CleanTextTests {

    private static final String COMBINING_ACUTE = Character.toString(0x0301);
    private static final String LINE_SEPARATOR = Character.toString(0x2028);
    private static final String NO_BREAK_SPACE = Character.toString(0x00A0);
    private static final String EM_SPACE = Character.toString(0x2003);
    private static final String BELL = Character.toString(0x0007);
    private static final String BYTE_ORDER_MARK = Character.toString(0xFEFF);
    private static final String ZERO_WIDTH_SPACE = Character.toString(0x200B);
    private static final String ZERO_WIDTH_NON_JOINER = Character.toString(0x200C);
    private static final String ZERO_WIDTH_JOINER = Character.toString(0x200D);
    private static final String BOOKS = Character.toString(0x1F4DA);

    @Test
    void composesAccentsInNfc() {
        assertThat(CleanText.of("cafe" + COMBINING_ACUTE)).isEqualTo("café");
    }

    @Test
    void unifiesLineBreaks() {
        assertThat(CleanText.of("uno\r\ndos\rtres" + LINE_SEPARATOR + "cuatro")).isEqualTo("uno\ndos\ntres\ncuatro");
    }

    @Test
    void turnsTabsAndNonBreakingSpacesIntoSingleSpaces() {
        assertThat(CleanText.of("uno\tdos" + NO_BREAK_SPACE + "tres" + EM_SPACE + "cuatro   cinco"))
                .isEqualTo("uno dos tres cuatro cinco");
    }

    @Test
    void dropsControlAndZeroWidthCharacters() {
        String text = BYTE_ORDER_MARK + "ho" + BELL + "la" + ZERO_WIDTH_SPACE + " mu" + ZERO_WIDTH_NON_JOINER + "n"
                + ZERO_WIDTH_JOINER + "do";

        assertThat(CleanText.of(text)).isEqualTo("hola mundo");
    }

    @Test
    void removesSpacesAroundLineBreaks() {
        assertThat(CleanText.of("  uno  \n  dos  ")).isEqualTo("uno\ndos");
    }

    @Test
    void keepsAtMostOneBlankLine() {
        assertThat(CleanText.of("uno\n\n\n\n \n dos")).isEqualTo("uno\n\ndos");
    }

    @Test
    void stripsLeadingAndTrailingBlankLines() {
        assertThat(CleanText.of("\n\n  uno\n\n")).isEqualTo("uno");
    }

    @Test
    void blankTextBecomesEmpty() {
        assertThat(CleanText.of(" \t\r\n ")).isEmpty();
    }

    @Test
    void keepsCharactersOutsideTheBasicPlane() {
        assertThat(CleanText.of("nota " + BOOKS)).isEqualTo("nota " + BOOKS);
    }
}
