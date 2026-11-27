package com.podcuaderno.importing.domain.model;

/** A block of a read document, with its text already clean for listening. */
public sealed interface DocumentBlock permits HeadingBlock, ParagraphBlock {

    String text();
}
