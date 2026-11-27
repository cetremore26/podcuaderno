package com.podcuaderno.importing.domain.model;

import com.podcuaderno.importing.domain.exception.EmptyDocumentException;
import java.util.List;

/** Turns the blocks of a document into topics and concepts (HU-07, HU-09). Rules: ADR-0013. */
public final class StructureDetector {

    static final String INTRODUCTION = "Introducción";
    static final String UNTITLED_TOPIC = "Tema sin título";
    static final String UNTITLED_CONCEPT = "Concepto sin título";
    static final int TITLE_WORDS = 6;

    private StructureDetector() {}

    /** Pasted text: there is no file name to suggest a title. */
    public static DetectedStructure detect(RawDocument document) {
        return detect(document, "");
    }

    public static DetectedStructure detect(RawDocument document, String sourceName) {
        if (document.isEmpty()) {
            throw new EmptyDocumentException();
        }
        String suggestedTitle = SourceName.suggestedTitle(sourceName, document);
        boolean byHeadings = document.hasStructuralHeadings();
        List<DetectedTopic> topics =
                byHeadings ? byHeadings(document, suggestedTitle) : byParagraphs(document, suggestedTitle);
        DetectedStructure structure = new DetectedStructure(
                suggestedTitle,
                byHeadings ? StructureSource.HEADINGS : StructureSource.PARAGRAPHS,
                topics,
                document.omitted());
        ImportLimits.requireWithin(structure);
        return structure;
    }

    /** HU-09: one topic, one concept per paragraph titled with its first six words. */
    private static List<DetectedTopic> byParagraphs(RawDocument document, String topicTitle) {
        List<DetectedConcept> concepts = document.blocks().stream()
                .map(DocumentBlock::text)
                .filter(text -> !text.isEmpty())
                .map(text -> new DetectedConcept(Titles.firstWords(text, TITLE_WORDS, UNTITLED_CONCEPT), text))
                .toList();
        return List.of(new DetectedTopic(topicTitle, concepts));
    }

    /** HU-07: {@code #} opens a topic and {@code ##} a concept; deeper headings stay as text. */
    private static List<DetectedTopic> byHeadings(RawDocument document, String suggestedTitle) {
        OutlineBuilder outline = new OutlineBuilder(document.hasTopicHeadings() ? INTRODUCTION : suggestedTitle);
        for (DocumentBlock block : document.blocks()) {
            switch (block) {
                case HeadingBlock heading
                when heading.level() == 1 -> outline.startTopic(Titles.fit(heading.text(), UNTITLED_TOPIC));
                case HeadingBlock heading
                when heading.level() == 2 -> outline.startConcept(Titles.fit(heading.text(), UNTITLED_CONCEPT));
                default -> outline.addText(block.text()); // paragraphs, lists and headings of level 3 or more
            }
        }
        return outline.build();
    }
}
