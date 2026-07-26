package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for PhoneNumber")
class PhoneNumberTest {

    @Test
    @DisplayName("Should create valid phone number with plus prefix")
    void of_validPlusPrefix_createsPhoneNumber() {
        PhoneNumber phone = PhoneNumber.of("+1234567890");

        assertEquals("+1234567890", phone.getValue());
    }

    @Test
    @DisplayName("Should create valid phone number without plus prefix")
    void of_validNumberWithoutPlus_createsPhoneNumber() {
        PhoneNumber phone = PhoneNumber.of("1234567890");

        assertEquals("1234567890", phone.getValue());
    }

    @Test
    @DisplayName("Should create valid phone number with spaces")
    void of_numberWithSpaces_createsPhoneNumber() {
        PhoneNumber phone = PhoneNumber.of("+1 234 567 890");

        assertEquals("+1 234 567 890", phone.getValue());
    }

    @Test
    @DisplayName("Should create valid phone number with dashes")
    void of_numberWithDashes_createsPhoneNumber() {
        PhoneNumber phone = PhoneNumber.of("+1-234-567-890");

        assertEquals("+1-234-567-890", phone.getValue());
    }

    @Test
    @DisplayName("Should trim whitespace from input")
    void of_withWhitespace_trimsInput() {
        PhoneNumber phone = PhoneNumber.of("  +1234567890 ");

        assertEquals("+1234567890", phone.getValue());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                PhoneNumber.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void of_blankValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                PhoneNumber.of("")
        );
    }

    @Test
    @DisplayName("Should throw exception when number contains no digits")
    void of_noDigits_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                PhoneNumber.of("abcde")
        );
    }

    @Test
    @DisplayName("Should throw exception when too short (less than 5 digits)")
    void of_tooShort_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                PhoneNumber.of("+1234")
        );
    }

    @Test
    @DisplayName("Should throw exception when too long (more than 15 digits for E.164)")
    void of_tooLong_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                PhoneNumber.of("+12345678901234567")
        );
    }

    @Test
    @DisplayName("Should be equal to another phone number with same value")
    void equals_sameValue_returnsTrue() {
        PhoneNumber p1 = PhoneNumber.of("+1234567890");
        PhoneNumber p2 = PhoneNumber.of("+1234567890");

        assertTrue(p1.equals(p2));
    }

    @Test
    @DisplayName("Should not equal phone number with different value")
    void equals_differentValue_returnsFalse() {
        PhoneNumber p1 = PhoneNumber.of("+1234567890");
        PhoneNumber p2 = PhoneNumber.of("+9876543210");

        assertFalse(p1.equals(p2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        PhoneNumber p1 = PhoneNumber.of("+1234567890");
        PhoneNumber p2 = PhoneNumber.of("+1234567890");

        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
