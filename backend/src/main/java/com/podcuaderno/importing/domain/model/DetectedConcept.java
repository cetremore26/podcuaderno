package com.podcuaderno.importing.domain.model;

import java.util.Objects;

/** A proposed concept: its title and the text to listen to, which may be empty. */
public record DetectedConcept(String title, String text) {

    public DetectedConcept {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(text, "text must not be null");
    }
}
