package com.podcuaderno.importing.domain.model;

import java.util.Objects;

/** A paragraph, a whole list or a quote of a read document. */
public record ParagraphBlock(String text) implements DocumentBlock {

    public ParagraphBlock {
        text = CleanText.of(Objects.requireNonNull(text, "text must not be null"));
    }
}
