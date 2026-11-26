package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class OpaqueTokenTests {

    @Test
    void generatesDistinctUrlSafeValuesOf32Bytes() {
        OpaqueToken first = OpaqueToken.generate();
        OpaqueToken second = OpaqueToken.generate();

        assertThat(first).isNotEqualTo(second);
        assertThat(first.value()).hasSize(43).matches("[A-Za-z0-9_-]+");
    }

    @Test
    void hashIsDeterministicSha256InHex() {
        OpaqueToken token = OpaqueToken.of("abc");

        assertThat(token.hash()).isEqualTo(TokenHash.of("abc"));
        assertThat(token.hash().value()).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    @Test
    void tokensWithTheSameValueAreEqual() {
        assertThat(OpaqueToken.of("abc")).isEqualTo(OpaqueToken.of("abc")).hasSameHashCodeAs(OpaqueToken.of("abc"));
        assertThat(OpaqueToken.of("abc")).isNotEqualTo(OpaqueToken.of("abd"));
    }

    @Test
    void toStringNeverRevealsTheValue() {
        assertThat(OpaqueToken.of("secret-value")).hasToString("OpaqueToken[***]");
    }

    @Test
    void rejectsNullValues() {
        assertThatThrownBy(() -> OpaqueToken.of(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TokenHash.of(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TokenHash(null)).isInstanceOf(NullPointerException.class);
    }
}
