package com.podcuaderno.importing.domain.model;

import java.util.Locale;
import java.util.Optional;

/** Formats that can be imported. The browser MIME type is never used: only the extension (ADR-0013). */
public enum DocumentFormat {
    PLAIN_TEXT,
    MARKDOWN;

    private static final int MAX_HEADING_INDENT = 3;

    /** {@code .txt} is plain text; {@code .md} and {@code .markdown} are Markdown; anything else is empty. */
    public static Optional<DocumentFormat> fromFileName(String fileName) {
        String name = fileName.toLowerCase(Locale.ROOT);
        if (name.endsWith(".txt")) {
            return Optional.of(PLAIN_TEXT);
        }
        if (name.endsWith(".md") || name.endsWith(".markdown")) {
            return Optional.of(MARKDOWN);
        }
        return Optional.empty();
    }

    /** Pasted text is Markdown when a line starts with a {@code #} or {@code ##} heading; else plain text. */
    public static DocumentFormat sniff(String pastedText) {
        int start = 0;
        while (start <= pastedText.length()) {
            int end = pastedText.indexOf('\n', start);
            if (end < 0) {
                end = pastedText.length();
            }
            if (isStructuralHeading(pastedText.substring(start, end))) {
                return MARKDOWN;
            }
            start = end + 1;
        }
        return PLAIN_TEXT;
    }

    private static boolean isStructuralHeading(String line) {
        int indent = 0;
        while (indent < line.length() && indent < MAX_HEADING_INDENT && line.charAt(indent) == ' ') {
            indent++;
        }
        String marker = line.substring(indent).stripTrailing();
        return marker.equals("#")
                || marker.equals("##")
                || line.startsWith("# ", indent)
                || line.startsWith("## ", indent);
    }
}
