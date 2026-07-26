package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for TransactionStatus")
class TransactionStatusTest {

    @Test
    @DisplayName("Should provide PENDING constant")
    void pending_constantExists() {
        assertEquals("PENDING", TransactionStatus.PENDING.getValue());
    }

    @Test
    @DisplayName("Should provide COMPLETED constant")
    void completed_constantExists() {
        assertEquals("COMPLETED", TransactionStatus.COMPLETED.getValue());
    }

    @Test
    @DisplayName("Should provide REJECTED constant")
    void rejected_constantExists() {
        assertEquals("REJECTED", TransactionStatus.REJECTED.getValue());
    }

    @Test
    @DisplayName("Should provide RETURNED constant")
    void returned_constantExists() {
        assertEquals("RETURNED", TransactionStatus.RETURNED.getValue());
    }

    @Test
    @DisplayName("Should create status from valid string value")
    void of_validValue_createsStatus() {
        TransactionStatus status = TransactionStatus.of("PENDING");

        assertEquals(TransactionStatus.PENDING, status);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionStatus.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid status format")
    void of_invalidFormat_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionStatus.of("INVALID_STATUS")
        );
    }

    @Test
    @DisplayName("Should be equal to another status with same value")
    void equals_sameValue_returnsTrue() {
        assertTrue(TransactionStatus.COMPLETED.equals(TransactionStatus.of("COMPLETED")));
    }

    @Test
    @DisplayName("Should not equal status with different value")
    void equals_differentValue_returnsFalse() {
        assertFalse(TransactionStatus.PENDING.equals(TransactionStatus.REJECTED));
    }
}
