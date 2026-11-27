package com.podcuaderno.importing.domain.model;

import java.util.List;
import java.util.Objects;

/** A document read by a {@code DocumentReader}: clean blocks in order, plus what was left out. */
public record RawDocument(List<DocumentBlock> blocks, List<OmittedElement> omitted) {

    public RawDocument {
        blocks = Objects.requireNonNull(blocks, "blocks must not be null").stream()
                .filter(block -> !(block instanceof ParagraphBlock paragraph
                        && paragraph.text().isEmpty()))
                .toList();
        omitted = List.copyOf(Objects.requireNonNull(omitted, "omitted must not be null"));
    }

    /** True without blocks, or when every block is a heading without text. */
    public boolean isEmpty() {
        return blocks.stream().allMatch(block -> block.text().isEmpty());
    }

    /** Headings of level 1 or 2 shape the notebook; without them each paragraph is a concept (HU-09). */
    public boolean hasStructuralHeadings() {
        return blocks.stream().anyMatch(block -> block instanceof HeadingBlock heading && heading.level() <= 2);
    }

    public boolean hasTopicHeadings() {
        return blocks.stream().anyMatch(block -> block instanceof HeadingBlock heading && heading.level() == 1);
    }
}
