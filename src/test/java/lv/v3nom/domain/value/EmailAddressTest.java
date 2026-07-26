package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for EmailAddress")
class EmailAddressTest {

    @Test
    @DisplayName("Should create valid email address")
    void of_validEmail_createsEmailAddress() {
        EmailAddress email = EmailAddress.of("user@example.com");

        assertEquals("user@example.com", email.getValue());
    }

    @Test
    @DisplayName("Should create valid email with subdomain")
    void of_subdomain_createsValidAddress() {
        EmailAddress email = EmailAddress.of("user@mail.example.com");

        assertEquals("user@mail.example.com", email.getValue());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                EmailAddress.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void of_blankValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                EmailAddress.of("")
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid email (no @)")
    void of_noAtSymbol_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                EmailAddress.of("userexample.com")
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid email (no dot in domain)")
    void of_noDotInDomain_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                EmailAddress.of("user@examplecom")
        );
    }

    @Test
    @DisplayName("Should be equal to another email address with same value")
    void equals_sameValue_returnsTrue() {
        EmailAddress e1 = EmailAddress.of("test@test.com");
        EmailAddress e2 = EmailAddress.of("test@test.com");

        assertTrue(e1.equals(e2));
    }

    @Test
    @DisplayName("Should not equal email address with different value")
    void equals_differentValue_returnsFalse() {
        EmailAddress e1 = EmailAddress.of("user1@example.com");
        EmailAddress e2 = EmailAddress.of("user2@example.com");

        assertFalse(e1.equals(e2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        EmailAddress e1 = EmailAddress.of("same@address.com");
        EmailAddress e2 = EmailAddress.of("same@address.com");

        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
