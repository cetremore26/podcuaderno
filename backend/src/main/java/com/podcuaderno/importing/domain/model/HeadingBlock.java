package com.podcuaderno.importing.domain.model;

import java.util.Objects;

/** A heading of level 1 to 6: {@code #} is a topic and {@code ##} a concept (ADR-0013). */
public record HeadingBlock(int level, String text) implements DocumentBlock {

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 6;

    public HeadingBlock {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException("level must be between 1 and 6");
        }
        text = CleanText.of(Objects.requireNonNull(text, "text must not be null"));
    }
}
