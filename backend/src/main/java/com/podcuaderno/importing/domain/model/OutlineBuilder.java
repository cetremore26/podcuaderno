package com.podcuaderno.importing.domain.model;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** Mutable helper of {@link StructureDetector}: keeps the topic and concept being filled. */
final class OutlineBuilder {

    private static final String PARAGRAPH_SEPARATOR = "\n\n";

    private final String implicitTopicTitle;
    private final List<DetectedTopic> topics = new ArrayList<>();
    private final List<DetectedConcept> concepts = new ArrayList<>();
    private final List<String> paragraphs = new ArrayList<>();
    private @Nullable String topicTitle;
    private @Nullable String conceptTitle;

    OutlineBuilder(String implicitTopicTitle) {
        this.implicitTopicTitle = implicitTopicTitle;
    }

    void startTopic(String title) {
        closeTopic();
        topicTitle = title;
    }

    void startConcept(String title) {
        ensureTopic();
        closeConcept();
        conceptTitle = title;
    }

    /** Text before the first {@code ##} of a topic becomes a concept named like the topic. */
    void addText(String text) {
        if (text.isEmpty()) {
            return;
        }
        String topic = ensureTopic();
        if (conceptTitle == null) {
            conceptTitle = topic;
        }
        paragraphs.add(text);
    }

    List<DetectedTopic> build() {
        closeTopic();
        return List.copyOf(topics);
    }

    private String ensureTopic() {
        String title = topicTitle;
        if (title == null) {
            title = implicitTopicTitle;
            topicTitle = title;
        }
        return title;
    }

    private void closeConcept() {
        if (conceptTitle != null) {
            concepts.add(new DetectedConcept(conceptTitle, String.join(PARAGRAPH_SEPARATOR, paragraphs)));
        }
        conceptTitle = null;
        paragraphs.clear();
    }

    private void closeTopic() {
        closeConcept();
        if (topicTitle != null) {
            topics.add(new DetectedTopic(topicTitle, concepts));
        }
        topicTitle = null;
        concepts.clear();
    }
}
