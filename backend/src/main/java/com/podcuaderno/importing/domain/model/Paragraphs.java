package com.podcuaderno.importing.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Paragraphs of plain text (ADR-0013): blocks separated by blank lines or, when the text has no blank line, one
 * paragraph per line. Loops instead of regular expressions.
 */
public final class Paragraphs {

    private static final char NEW_LINE = '\n';

    private Paragraphs() {}

    public static List<String> split(String text) {
        List<String> lines = lines(text);
        if (lines.stream().noneMatch(String::isBlank)) {
            return List.copyOf(lines);
        }
        List<String> paragraphs = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                addParagraph(paragraphs, current);
            } else {
                current.add(line);
            }
        }
        addParagraph(paragraphs, current);
        return List.copyOf(paragraphs);
    }

    private static void addParagraph(List<String> paragraphs, List<String> current) {
        if (!current.isEmpty()) {
            paragraphs.add(String.join("\n", current));
            current.clear();
        }
    }

    /** The lines from the first to the last that are not blank, with any line break convention. */
    private static List<String> lines(String text) {
        String normalized = text.replace("\r\n", "\n").replace('\r', NEW_LINE).strip();
        List<String> lines = new ArrayList<>();
        if (normalized.isEmpty()) {
            return lines;
        }
        int start = 0;
        int end = normalized.indexOf(NEW_LINE);
        while (end >= 0) {
            lines.add(normalized.substring(start, end));
            start = end + 1;
            end = normalized.indexOf(NEW_LINE, start);
        }
        lines.add(normalized.substring(start));
        return lines;
    }
}
