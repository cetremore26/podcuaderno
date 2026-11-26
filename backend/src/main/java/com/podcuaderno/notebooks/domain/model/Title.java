package com.podcuaderno.notebooks.domain.model;

import com.podcuaderno.notebooks.domain.exception.InvalidTitleException;
import java.util.Objects;

/** Title of a notebook, topic or concept: trimmed, between 1 and 150 characters. */
public record Title(String value) {

    public static final int MAX_LENGTH = 150;

    public Title {
        Objects.requireNonNull(value, "value must not be null");
        value = value.strip();
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            throw new InvalidTitleException();
        }
    }
}
