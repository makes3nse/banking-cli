package lv.v3nom.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for TransactionMapper")
class TransactionMapperTest {

    private lv.v3nom.domain.model.Transaction transaction;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
        completedAt = LocalDateTime.of(2025, 1, 2, 8, 0);

        // Create a complete transfer transaction
        lv.v3nom.domain.value.TransactionId id = lv.v3nom.domain.value.TransactionId.generate();
        lv.v3nom.domain.value.Currency currency = lv.v3nom.domain.value.Currency.EUR;
        lv.v3nom.domain.value.Money amount = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(250).setScale(2), currency);
        lv.v3nom.domain.value.AccountId source = lv.v3nom.domain.value.AccountId.of("111111111111111");
        lv.v3nom.domain.value.AccountId target = lv.v3nom.domain.value.AccountId.of("222222222222222");
        lv.v3nom.domain.value.TransactionType type = lv.v3nom.domain.value.TransactionType.TRANSFER;

        transaction = lv.v3nom.domain.model.Transaction.reconstitute(
                id, currency, amount, source, target, type,
                lv.v3nom.domain.value.TransactionStatus.COMPLETED, createdAt, completedAt, null, null);
    }

    @Test
    @DisplayName("Should convert Transaction to response with success status")
    void toResponse_transactionWithCompletedStatus_returnsTransactionResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("SUCCESS");

        var response = TransactionMapper.toResponse(transaction, opStatus);

        assertEquals(transaction.getTransactionId().getValue(), response.getTransactionId());
        assertEquals("TRANSFER", response.getType());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("111111111111111", response.getSourceAccountId());
        assertEquals("222222222222222", response.getTargetAccountId());
        assertEquals("EUR", response.getCurrency());
        assertEquals(BigDecimal.valueOf(250).setScale(2), response.getAmount());
        assertNull(response.getFailureReason());
        assertEquals(transaction.getCreatedAt().toString(), response.getCreatedAt());
        assertEquals(transaction.getCompletedAt().toString(), response.getCompletedAt());
        assertEquals("SUCCESS", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should convert Transaction to summary response")
    void toSummaryResponse_returnsTransactionSummaryResponse() {
        var response = TransactionMapper.toSummaryResponse(transaction);

        assertEquals(transaction.getTransactionId().getValue(), response.getTransactionId());
        assertEquals("TRANSFER", response.getType());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("EUR", response.getCurrency());
        assertEquals(BigDecimal.valueOf(250).setScale(2), response.getAmount());
        assertEquals(transaction.getCreatedAt().toString(), response.getCreatedAt());
    }

    @Test
    @DisplayName("Should create failure response for transaction")
    void failureResponse_returnsFailureTransactionResponse() {
        lv.v3nom.domain.value.TransactionType type = lv.v3nom.domain.value.TransactionType.DEPOSIT;
        String reason = "Insufficient funds";
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = TransactionMapper.failureResponse(type, reason, opStatus);

        assertNull(response.getTransactionId());
        assertEquals("DEP", response.getType());
        //assertNull(response.getType());
        assertNull(response.getStatus());
        assertNull(response.getSourceAccountId());
        assertNull(response.getTargetAccountId());
        assertNull(response.getCurrency());
        assertNull(response.getAmount());
        assertEquals(reason, response.getFailureReason());
        assertNull(response.getCreatedAt());
        assertNull(response.getCompletedAt());
        assertEquals("FAILURE", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should handle null transaction type in failure response")
    void failureResponse_nullTransactionType_handlesCorrectly() {
        String reason = "Error occurred";
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = TransactionMapper.failureResponse(null, reason, opStatus);

        assertNull(response.getTransactionId());
        assertNull(response.getType()); // null type handled gracefully
        assertEquals(reason, response.getFailureReason());
    }
}
