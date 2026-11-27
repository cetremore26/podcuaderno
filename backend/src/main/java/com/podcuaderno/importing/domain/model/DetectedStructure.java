package com.podcuaderno.importing.domain.model;

import java.util.List;
import java.util.Objects;

/** The preview of an import: nothing is stored until the student confirms it (docs/05 §5.5). */
public record DetectedStructure(
        String suggestedTitle, StructureSource source, List<DetectedTopic> topics, List<OmittedElement> omitted) {

    public DetectedStructure {
        Objects.requireNonNull(suggestedTitle, "suggestedTitle must not be null");
        Objects.requireNonNull(source, "source must not be null");
        topics = List.copyOf(Objects.requireNonNull(topics, "topics must not be null"));
        omitted = List.copyOf(Objects.requireNonNull(omitted, "omitted must not be null"));
    }

    public int topicCount() {
        return topics.size();
    }

    public int conceptCount() {
        return topics.stream().mapToInt(topic -> topic.concepts().size()).sum();
    }

    /** RNF-11 counts the text that is listened to: the UTF-16 length of every concept text (ADR-0013). */
    public int characterCount() {
        return topics.stream()
                .flatMap(topic -> topic.concepts().stream())
                .mapToInt(concept -> concept.text().length())
                .sum();
    }
}
