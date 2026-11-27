package com.podcuaderno.importing.domain.model;

/** Elements that cannot be listened to, so they are left out of the notebook and reported (ADR-0013). */
public enum OmittedElementKind {
    CODE_BLOCK,
    IMAGE,
    TABLE,
    HTML
}
