package com.podcuaderno.importing.domain.model;

/** Suggested notebook title from the name of the uploaded file. Works on strings only, never on paths. */
public final class SourceName {

    public static final String DEFAULT_TITLE = "Mis apuntes";

    private SourceName() {}

    /** The file name without folders or extension; else the first topic heading; else "Mis apuntes". */
    public static String suggestedTitle(String sourceName, RawDocument document) {
        String fromName = fromFileName(sourceName);
        if (!fromName.isEmpty()) {
            return Titles.fit(fromName, DEFAULT_TITLE);
        }
        String firstTopic = document.blocks().stream()
                .filter(block -> block instanceof HeadingBlock heading && heading.level() == 1)
                .map(DocumentBlock::text)
                .filter(text -> !text.isEmpty())
                .findFirst()
                .orElse(DEFAULT_TITLE);
        return Titles.fit(firstTopic, DEFAULT_TITLE);
    }

    private static String fromFileName(String sourceName) {
        int folder = Math.max(sourceName.lastIndexOf('/'), sourceName.lastIndexOf('\\'));
        String name = sourceName.substring(folder + 1);
        int extension = name.lastIndexOf('.');
        if (extension >= 0) {
            name = name.substring(0, extension);
        }
        String words = CleanText.of(name.replace('_', ' ').replace('-', ' ').replace('\n', ' '));
        if (words.isEmpty()) {
            return words;
        }
        int first = words.codePointAt(0);
        return new StringBuilder()
                .appendCodePoint(Character.toTitleCase(first))
                .append(words, Character.charCount(first), words.length())
                .toString();
    }
}
