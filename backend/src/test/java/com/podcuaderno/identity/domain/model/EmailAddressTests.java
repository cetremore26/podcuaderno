package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.domain.exception.InvalidEmailAddressException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailAddressTests {

    private static final String DOMAIN = "@correo.com";

    @Test
    void normalisesSpacesAndCase() {
        assertThat(new EmailAddress("  Ana.Perez@Correo.COM ").value()).isEqualTo("ana.perez@correo.com");
    }

    @Test
    void equalAddressesAfterNormalisationAreEqual() {
        assertThat(new EmailAddress("ANA@correo.com")).isEqualTo(new EmailAddress("ana@correo.com"));
    }

    @Test
    void acceptsTheMaximumLength() {
        String address = "a".repeat(EmailAddress.MAX_LENGTH - DOMAIN.length()) + DOMAIN;

        assertThat(new EmailAddress(address).value()).hasSize(EmailAddress.MAX_LENGTH);
    }

    @Test
    void rejectsAddressesLongerThanTheMaximum() {
        String address = "a".repeat(EmailAddress.MAX_LENGTH - DOMAIN.length() + 1) + DOMAIN;

        assertThatThrownBy(() -> new EmailAddress(address)).isInstanceOf(InvalidEmailAddressException.class);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                "ana",
                "@correo.com",
                "ana@",
                "ana@@correo.com",
                "ana@correo@com",
                "ana@correo",
                "ana@.com",
                "ana@correo.",
                "ana maria@correo.com"
            })
    void rejectsMalformedAddresses(String address) {
        assertThatThrownBy(() -> new EmailAddress(address))
                .isInstanceOf(InvalidEmailAddressException.class)
                .extracting("code")
                .isEqualTo("invalid-email");
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new EmailAddress(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void toStringHidesTheAddress() {
        assertThat(new EmailAddress("ana@correo.com")).hasToString("EmailAddress[***]");
    }
}
