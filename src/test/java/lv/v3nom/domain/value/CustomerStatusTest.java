package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for CustomerStatus")
class CustomerStatusTest {

    @Test
    @DisplayName("Should provide PENDING_VERIFICATION constant")
    void pendingVerification_constantExists() {
        assertEquals("PENDING_VERIFICATION", CustomerStatus.PENDING_VERIFICATION.getValue());
    }

    @Test
    @DisplayName("Should provide ACTIVE constant")
    void active_constantExists() {
        assertEquals("ACTIVE", CustomerStatus.ACTIVE.getValue());
    }

    @Test
    @DisplayName("Should provide SUSPENDED constant")
    void suspended_constantExists() {
        assertEquals("SUSPENDED", CustomerStatus.SUSPENDED.getValue());
    }

    @Test
    @DisplayName("Should provide BANNED constant")
    void banned_constantExists() {
        assertEquals("BANNED", CustomerStatus.BANNED.getValue());
    }

    @Test
    @DisplayName("Should provide CLOSED constant")
    void closed_constantExists() {
        assertEquals("CLOSED", CustomerStatus.CLOSED.getValue());
    }

    @Test
    @DisplayName("Should create status from valid string value")
    void of_validValue_createsStatus() {
        CustomerStatus status = CustomerStatus.of("ACTIVE");

        assertEquals(CustomerStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                CustomerStatus.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid status format")
    void of_invalidFormat_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                CustomerStatus.of("INVALID_STATUS")
        );
    }

    @Test
    @DisplayName("Should be equal to another status with same value")
    void equals_sameValue_returnsTrue() {
        assertTrue(CustomerStatus.ACTIVE.equals(CustomerStatus.of("ACTIVE")));
    }

    @Test
    @DisplayName("Should not equal status with different value")
    void equals_differentValue_returnsFalse() {
        assertFalse(CustomerStatus.ACTIVE.equals(CustomerStatus.SUSPENDED));
    }
}
