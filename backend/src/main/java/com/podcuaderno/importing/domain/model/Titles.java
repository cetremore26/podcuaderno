package com.podcuaderno.importing.domain.model;

import java.util.ArrayList;
import java.util.List;

/** Titles of detected topics and concepts: never empty and at most 150 characters (ADR-0013). */
final class Titles {

    private static final String ELLIPSIS = "…";
    private static final String TRAILING_PUNCTUATION = ".,;:…";

    private Titles() {}

    /** The text on one line, or the fallback when blank; a longer text is cut at a word and ends with an ellipsis. */
    static String fit(String text, String fallback) {
        String title = text.replace('\n', ' ').strip();
        if (title.isEmpty()) {
            return fallback;
        }
        if (title.length() <= ImportLimits.MAX_TITLE_LENGTH) {
            return title;
        }
        int limit = ImportLimits.MAX_TITLE_LENGTH - ELLIPSIS.length();
        int cut = title.lastIndexOf(' ', limit);
        if (cut <= 0) {
            cut = Character.isHighSurrogate(title.charAt(limit - 1)) ? limit - 1 : limit;
        }
        return title.substring(0, cut).strip() + ELLIPSIS;
    }

    /** The first words of a paragraph, without the punctuation that closes them (HU-09). */
    static String firstWords(String text, int count, String fallback) {
        List<String> words = new ArrayList<>(count);
        int index = 0;
        while (index < text.length() && words.size() < count) {
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            int start = index;
            while (index < text.length() && !Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            if (index > start) {
                words.add(text.substring(start, index));
            }
        }
        String title = String.join(" ", words);
        int end = title.length();
        while (end > 0 && TRAILING_PUNCTUATION.indexOf(title.charAt(end - 1)) >= 0) {
            end--;
        }
        return fit(title.substring(0, end), fallback);
    }
}
