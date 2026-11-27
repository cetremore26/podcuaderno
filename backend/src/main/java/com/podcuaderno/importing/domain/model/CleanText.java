package com.podcuaderno.importing.domain.model;

import java.text.Normalizer;

/**
 * Text ready to be listened to (ADR-0013): NFC, single spaces, no control or zero-width characters, no spaces around
 * line breaks and at most one blank line in a row. One pass over the characters, without regular expressions.
 */
public final class CleanText {

    private static final char NEW_LINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final int MAX_NEW_LINES = 2;

    private CleanText() {}

    public static String of(String raw) {
        String text = Normalizer.normalize(raw, Normalizer.Form.NFC);
        StringBuilder clean = new StringBuilder(text.length());
        int pendingNewLines = 0;
        boolean pendingSpace = false;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (isLineBreak(character)) {
                if (!isCarriageReturnBeforeNewLine(text, index)) {
                    pendingNewLines++;
                }
                pendingSpace = false; // no spaces at the end of a line
            } else if (isSpace(character)) {
                pendingSpace = true;
            } else if (!isInvisible(character)) {
                appendSeparator(clean, pendingNewLines, pendingSpace);
                pendingNewLines = 0;
                pendingSpace = false;
                clean.append(character);
            }
        }
        return clean.toString();
    }

    /** Separators before the first character are dropped, and spaces at the start of a line too. */
    private static void appendSeparator(StringBuilder clean, int pendingNewLines, boolean pendingSpace) {
        if (clean.isEmpty()) {
            return;
        }
        if (pendingNewLines > 0) {
            clean.repeat(NEW_LINE, Math.min(pendingNewLines, MAX_NEW_LINES));
        } else if (pendingSpace) {
            clean.append(' ');
        }
    }

    private static boolean isCarriageReturnBeforeNewLine(String text, int index) {
        return text.charAt(index) == CARRIAGE_RETURN && index + 1 < text.length() && text.charAt(index + 1) == NEW_LINE;
    }

    /** Line feeds, carriage returns and the Unicode line and paragraph separators. */
    private static boolean isLineBreak(char character) {
        int type = Character.getType(character);
        return character == NEW_LINE
                || character == CARRIAGE_RETURN
                || type == Character.LINE_SEPARATOR
                || type == Character.PARAGRAPH_SEPARATOR;
    }

    /** Tabs and every Unicode space, including the non-breaking one. */
    private static boolean isSpace(char character) {
        return Character.isWhitespace(character) || Character.getType(character) == Character.SPACE_SEPARATOR;
    }

    /** Control characters and format characters such as zero-width spaces, joiners and the byte order mark. */
    private static boolean isInvisible(char character) {
        return Character.isISOControl(character) || Character.getType(character) == Character.FORMAT;
    }
}
