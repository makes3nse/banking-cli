package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for TransactionId")
class TransactionIdTest {

    @Test
    @DisplayName("Should generate a valid transaction ID")
    void generate_createsValidTransactionId() {
        TransactionId id = TransactionId.generate();

        assertNotNull(id);
        assertTrue(id.getValue().length() > 0);
    }

    @Test
    @DisplayName("Should create transaction ID from existing string value")
    void of_existingValue_createsTransactionId() {
        TransactionId id = TransactionId.of("abc123def456");

        assertEquals("abc123def456", id.getValue());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionId.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void of_blankValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionId.of("")
        );
    }

    @Test
    @DisplayName("Should be equal to another transaction ID with same value")
    void equals_sameValue_returnsTrue() {
        TransactionId id1 = TransactionId.of("sameValue123");
        TransactionId id2 = TransactionId.of("sameValue123");

        assertTrue(id1.equals(id2));
    }

    @Test
    @DisplayName("Should not equal transaction ID with different value")
    void equals_differentValue_returnsFalse() {
        TransactionId id1 = TransactionId.of("value111");
        TransactionId id2 = TransactionId.of("value222");

        assertFalse(id1.equals(id2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        TransactionId id1 = TransactionId.of("sameHashValue");
        TransactionId id2 = TransactionId.of("sameHashValue");

        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
