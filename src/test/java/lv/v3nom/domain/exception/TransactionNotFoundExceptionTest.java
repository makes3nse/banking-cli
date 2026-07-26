package lv.v3nom.domain.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for TransactionNotFoundException")
class TransactionNotFoundExceptionTest {

    private lv.v3nom.domain.value.TransactionId transactionId;
    private lv.v3nom.domain.value.AccountId accountId;
    private lv.v3nom.domain.value.TransactionType transactionType;
    private lv.v3nom.domain.value.TransactionStatus transactionStatus;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        transactionId = lv.v3nom.domain.value.TransactionId.generate();
        accountId = lv.v3nom.domain.value.AccountId.of("123456789012345");
        transactionType = lv.v3nom.domain.value.TransactionType.TRANSFER;
        transactionStatus = lv.v3nom.domain.value.TransactionStatus.PENDING;
        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
    }

    @Test
    @DisplayName("Default constructor should produce correct message")
    void defaultConstructor_messageIsCorrect() {
        TransactionNotFoundException ex = new TransactionNotFoundException();

        assertEquals("Transaction not found", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor with TransactionId should set transactionId correctly")
    void constructorWithTransactionId_setsFields() {
        lv.v3nom.domain.value.TransactionId id = lv.v3nom.domain.value.TransactionId.generate();
        TransactionNotFoundException ex = new TransactionNotFoundException(id);

        assertEquals(id, ex.getTransactionId());
        assertNull(ex.getAccountId());
        assertNull(ex.getTransactionType());
        assertNull(ex.getTransactionStatus());
        assertNull(ex.getCreatedAt());
    }

    @Test
    @DisplayName("Constructor with AccountId should set accountId correctly")
    void constructorWithAccountId_setsFields() {
        TransactionNotFoundException ex = new TransactionNotFoundException(accountId);

        assertNull(ex.getTransactionId());
        assertEquals(accountId, ex.getAccountId());
        assertNull(ex.getTransactionType());
        assertNull(ex.getTransactionStatus());
        assertNull(ex.getCreatedAt());
    }

    @Test
    @DisplayName("Constructor with TransactionType should set transactionType correctly")
    void constructorWithTransactionType_setsFields() {
        TransactionNotFoundException ex = new TransactionNotFoundException(transactionType);

        assertNull(ex.getTransactionId());
        assertNull(ex.getAccountId());
        assertEquals(transactionType, ex.getTransactionType());
        assertNull(ex.getTransactionStatus());
        assertNull(ex.getCreatedAt());
    }

    @Test
    @DisplayName("Constructor with TransactionStatus should set transactionStatus correctly")
    void constructorWithTransactionStatus_setsFields() {
        TransactionNotFoundException ex = new TransactionNotFoundException(transactionStatus);

        assertNull(ex.getTransactionId());
        assertNull(ex.getAccountId());
        assertNull(ex.getTransactionType());
        assertEquals(transactionStatus, ex.getTransactionStatus());
        assertNull(ex.getCreatedAt());
    }

    @Test
    @DisplayName("Constructor with LocalDateTime should set createdAt correctly")
    void constructorWithCreatedAt_setsFields() {
        LocalDateTime created = LocalDateTime.of(2025, 6, 15, 8, 30);
        TransactionNotFoundException ex = new TransactionNotFoundException(created);

        assertNull(ex.getTransactionId());
        assertNull(ex.getAccountId());
        assertNull(ex.getTransactionType());
        assertNull(ex.getTransactionStatus());
        assertEquals(created, ex.getCreatedAt());
    }
}
