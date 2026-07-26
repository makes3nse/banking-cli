package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for Currency")
class CurrencyTest {

    @Test
    @DisplayName("Should create currency from valid string value")
    void of_validValue_createsCurrency() {
        Currency currency = Currency.of("usd");

        assertEquals("USD", currency.value());
    }

    @Test
    @DisplayName("Should normalize lowercase input to uppercase")
    void of_lowercase_normalizesToUppercase() {
        Currency currency = Currency.of("eur");

        assertEquals("EUR", currency.value());
    }

    @Test
    @DisplayName("Should create predefined USD currency")
    void usd_constant_createsCurrency() {
        Currency usd = Currency.USD;

        assertNotNull(usd);
        assertEquals("USD", usd.value());
    }

    @Test
    @DisplayName("Should create predefined EUR currency")
    void eur_constant_createsCurrency() {
        Currency eur = Currency.EUR;

        assertNotNull(eur);
        assertEquals("EUR", eur.value());
    }

    @Test
    @DisplayName("Should create predefined GBP currency")
    void gbp_constant_createsCurrency() {
        Currency gbp = Currency.GBP;

        assertNotNull(gbp);
        assertEquals("GBP", gbp.value());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(NullPointerException.class, () ->
                Currency.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid currency format (too short)")
    void of_tooShort_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Currency.of("US")
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid currency format (too long)")
    void of_tooLong_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Currency.of("USDXX")
        );
    }

    @Test
    @DisplayName("Should throw exception for non-alphabetic characters")
    void of_nonAlphabetic_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Currency.of("1US")
        );
    }

    @Test
    @DisplayName("Should equal another currency with same value")
    void equals_sameValue_returnsTrue() {
        Currency usd1 = Currency.of("usd");
        Currency usd2 = Currency.of("USD");

        assertTrue(usd1.equals(usd2));
    }

    @Test
    @DisplayName("Should not equal currency with different value")
    void equals_differentValue_returnsFalse() {
        Currency eur = Currency.EUR;
        Currency usd = Currency.USD;

        assertFalse(eur.equals(usd));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        Currency eur1 = Currency.of("eur");
        Currency eur2 = Currency.EUR;

        assertEquals(eur1.hashCode(), eur2.hashCode());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_returnsValue() {
        Currency currency = Currency.USD;

        assertEquals("USD", currency.toString());
    }

    @Test
    @DisplayName("Should get symbol for locale")
    void getSymbol_validLocale_returnsSymbol() {
        Currency usd = Currency.USD;

        assertNotNull(usd.getSymbol(Locale.US));
    }

    @Test
    @DisplayName("Should return correct default fraction digits")
    void getDefaultFractionDigits_correctValue() {
        Currency usd = Currency.USD;

        assertEquals(2, usd.getDefaultFractionDigits());
    }
}
