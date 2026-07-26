package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for TransactionType")
class TransactionTypeTest {

    @Test
    @DisplayName("Should provide DEPOSIT constant with correct properties")
    void deposit_constantHasCorrectProperties() {
        assertEquals("DEP", TransactionType.DEPOSIT.getTransactionCode());
        assertEquals("DEPOSIT", TransactionType.DEPOSIT.getTransactionName());
        assertTrue(TransactionType.DEPOSIT.affectsBalance());
        assertFalse(TransactionType.DEPOSIT.requiresRecipient());
    }

    @Test
    @DisplayName("Should provide WITHDRAW constant with correct properties")
    void withdraw_constantHasCorrectProperties() {
        assertEquals("WDR", TransactionType.WITHDRAW.getTransactionCode());
        assertEquals("WITHDRAW", TransactionType.WITHDRAW.getTransactionName());
        assertTrue(TransactionType.WITHDRAW.affectsBalance());
        assertFalse(TransactionType.WITHDRAW.requiresRecipient());
    }

    @Test
    @DisplayName("Should provide TRANSFER constant with correct properties")
    void transfer_constantHasCorrectProperties() {
        assertEquals("TFR", TransactionType.TRANSFER.getTransactionCode());
        assertEquals("TRANSFER", TransactionType.TRANSFER.getTransactionName());
        assertTrue(TransactionType.TRANSFER.affectsBalance());
        assertTrue(TransactionType.TRANSFER.requiresRecipient());
    }

    @Test
    @DisplayName("Should provide FEE constant with correct properties")
    void fee_constantHasCorrectProperties() {
        assertEquals("FEE", TransactionType.FEE.getTransactionCode());
        assertEquals("FEE", TransactionType.FEE.getTransactionName());
        assertTrue(TransactionType.FEE.affectsBalance());
        assertFalse(TransactionType.FEE.requiresRecipient());
    }

    @Test
    @DisplayName("Should provide INTEREST constant with correct properties")
    void interest_constantHasCorrectProperties() {
        assertEquals("INT", TransactionType.INTEREST.getTransactionCode());
        assertEquals("INTEREST", TransactionType.INTEREST.getTransactionName());
        assertTrue(TransactionType.INTEREST.affectsBalance());
        assertFalse(TransactionType.INTEREST.requiresRecipient());
    }

    @Test
    @DisplayName("Should create type from valid string value")
    void of_validValue_createsType() {
        TransactionType type = TransactionType.of("DEPOSIT");

        assertEquals(TransactionType.DEPOSIT, type);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionType.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid type format")
    void of_invalidFormat_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TransactionType.of("INVALID_TYPE")
        );
    }

    @Test
    @DisplayName("Should be equal to another type with same code")
    void equals_sameCode_returnsTrue() {
        assertTrue(TransactionType.TRANSFER.equals(TransactionType.of("TRANSFER")));
    }

    @Test
    @DisplayName("Should not equal type with different code")
    void equals_differentCode_returnsFalse() {
        assertFalse(TransactionType.DEPOSIT.equals(TransactionType.WITHDRAW));
    }

    @Test
    @DisplayName("Should return transaction name in toString")
    void toString_returnsTransactionName() {
        assertEquals("DEPOSIT", TransactionType.DEPOSIT.toString());
        assertEquals("WITHDRAW", TransactionType.WITHDRAW.toString());
        assertEquals("TRANSFER", TransactionType.TRANSFER.toString());
    }

    @Test
    @DisplayName("Should have consistent hash code")
    void hashCode_consistentWithEquals() {
        TransactionType deposit1 = TransactionType.of("DEPOSIT");
        TransactionType deposit2 = TransactionType.DEPOSIT;

        assertEquals(deposit1.hashCode(), deposit2.hashCode());
    }
}
