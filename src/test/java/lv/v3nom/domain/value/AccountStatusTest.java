package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AccountStatus")
class AccountStatusTest {

    @Test
    @DisplayName("Should provide PENDING_VERIFICATION constant")
    void pendingVerification_constantExists() {
        assertEquals("PENDING_VERIFICATION", AccountStatus.PENDING_VERIFICATION.getValue());
    }

    @Test
    @DisplayName("Should provide ACTIVE constant")
    void active_constantExists() {
        assertEquals("ACTIVE", AccountStatus.ACTIVE.getValue());
    }

    @Test
    @DisplayName("Should provide CLOSED constant")
    void closed_constantExists() {
        assertEquals("CLOSED", AccountStatus.CLOSED.getValue());
    }

    @Test
    @DisplayName("Should provide FROZEN constant")
    void frozen_constantExists() {
        assertEquals("FROZEN", AccountStatus.FROZEN.getValue());
    }

    @Test
    @DisplayName("Should provide BLOCKED constant")
    void blocked_constantExists() {
        assertEquals("BLOCKED", AccountStatus.BLOCKED.getValue());
    }

    @Test
    @DisplayName("Should create status from valid string value")
    void of_validValue_createsStatus() {
        AccountStatus status = AccountStatus.of("ACTIVE");

        assertEquals(AccountStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountStatus.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid status format")
    void of_invalidFormat_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountStatus.of("INVALID_STATUS")
        );
    }

    @Test
    @DisplayName("Should be equal to another status with same value")
    void equals_sameValue_returnsTrue() {
        assertTrue(AccountStatus.ACTIVE.equals(AccountStatus.of("ACTIVE")));
    }

    @Test
    @DisplayName("Should not equal status with different value")
    void equals_differentValue_returnsFalse() {
        assertFalse(AccountStatus.ACTIVE.equals(AccountStatus.CLOSED));
    }
}
