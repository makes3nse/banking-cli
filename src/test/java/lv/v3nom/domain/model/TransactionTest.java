package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for Transaction")
class TransactionTest {

    private AccountId targetAccountId;
    private AccountId sourceAccountId;
    private AccountId transferTargetAccount;
    private Money depositAmount;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        targetAccountId = AccountId.of("111111111111111");
        sourceAccountId = AccountId.of("222222222222222");
        transferTargetAccount = AccountId.of("333333333333333");
        depositAmount = Money.of(BigDecimal.valueOf(500).setScale(2), Currency.EUR);
        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
    }

    @Test
    @DisplayName("Should create a deposit transaction record")
    void createDepositRecord_createsPendingTransaction() {
        TransactionId transactionId = TransactionId.generate();

        Transaction deposit = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        assertEquals(transactionId, deposit.getTransactionId());
        assertEquals(depositAmount.getCurrency(), deposit.getCurrency());
        assertEquals(depositAmount, deposit.getAmount());
        assertEquals(targetAccountId, deposit.getTargetAccount());
        assertEquals(targetAccountId, deposit.getSourceAccount());
        assertEquals(TransactionType.DEPOSIT, deposit.getTransactionType());
        assertEquals(TransactionStatus.PENDING, deposit.getTransactionStatus());
        assertTrue(deposit.isPending());
        assertFalse(deposit.isComplete());
        assertFalse(deposit.isReturned());
        assertFalse(deposit.isRejected());
    }

    @Test
    @DisplayName("Should create a withdrawal transaction record")
    void createWithdrawalRecord_createsPendingTransaction() {
        TransactionId transactionId = TransactionId.generate();

        Transaction withdrawal = Transaction.createWithdrawalRecord(transactionId, depositAmount, sourceAccountId, createdAt);

        assertEquals(transactionId, withdrawal.getTransactionId());
        assertEquals(depositAmount.getCurrency(), withdrawal.getCurrency());
        assertEquals(depositAmount, withdrawal.getAmount());
        assertEquals(sourceAccountId, withdrawal.getSourceAccount());
        assertEquals(sourceAccountId, withdrawal.getTargetAccount());
        assertEquals(TransactionType.WITHDRAW, withdrawal.getTransactionType());
        assertEquals(TransactionStatus.PENDING, withdrawal.getTransactionStatus());
    }

    @Test
    @DisplayName("Should create a transfer transaction record")
    void createTransferRecord_createsPendingTransaction() {
        TransactionId transactionId = TransactionId.generate();

        Transaction transfer = Transaction.createTransferRecord(transactionId, depositAmount, sourceAccountId, transferTargetAccount, createdAt);

        assertEquals(transactionId, transfer.getTransactionId());
        assertEquals(depositAmount.getCurrency(), transfer.getCurrency());
        assertEquals(depositAmount, transfer.getAmount());
        assertEquals(sourceAccountId, transfer.getSourceAccount());
        assertEquals(transferTargetAccount, transfer.getTargetAccount());
        assertEquals(TransactionType.TRANSFER, transfer.getTransactionType());
        assertEquals(TransactionStatus.PENDING, transfer.getTransactionStatus());
    }

    @Test
    @DisplayName("Should complete a pending transaction")
    void complete_pendingTransaction_success() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        transaction.complete(completedAt);

        assertEquals(TransactionStatus.COMPLETED, transaction.getTransactionStatus());
        assertTrue(transaction.isComplete());
        assertFalse(transaction.isPending());
        assertEquals(completedAt, transaction.getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when completing non-pending transaction")
    void complete_completedTransaction_throwsException() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        transaction.complete(completedAt);

        assertThrows(IllegalStateException.class, () ->
                transaction.complete(completedAt)
        );
    }

    @Test
    @DisplayName("Should mark transaction as returned")
    void markReturned_pendingTransaction_success() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        String reason = "Insufficient funds in source account";
        transaction.markReturned(completedAt, reason);

        assertEquals(TransactionStatus.RETURNED, transaction.getTransactionStatus());
        assertTrue(transaction.isReturned());
        assertEquals(reason, transaction.getReturnReason());
        assertEquals(reason, transaction.getFailureReason());
        assertEquals(completedAt, transaction.getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when returning non-pending transaction")
    void markReturned_nonPendingTransaction_throwsException() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        String reason = "Error";
        transaction.complete(completedAt);

        assertThrows(IllegalStateException.class, () ->
                transaction.markReturned(completedAt, reason)
        );
    }

    @Test
    @DisplayName("Should reject a pending transaction")
    void reject_pendingTransaction_success() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        String reason = "Account not found";
        transaction.reject(completedAt, reason);

        assertEquals(TransactionStatus.REJECTED, transaction.getTransactionStatus());
        assertTrue(transaction.isRejected());
        assertEquals(reason, transaction.getRejectReason());
        assertEquals(reason, transaction.getFailureReason());
        assertEquals(completedAt, transaction.getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when rejecting non-pending transaction")
    void reject_nonPendingTransaction_throwsException() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        LocalDateTime completedAt = createdAt.plusHours(1);
        String reason = "Error";
        transaction.complete(completedAt);

        assertThrows(IllegalStateException.class, () ->
                transaction.reject(completedAt, reason)
        );
    }

    @Test
    @DisplayName("Should return correct failure reason for returned transaction")
    void getFailureReason_returnedTransaction_returnsReturnReason() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);
        LocalDateTime completedAt = createdAt.plusHours(1);
        String returnReason = "Reversed by customer";

        transaction.markReturned(completedAt, returnReason);

        assertEquals(returnReason, transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should return correct failure reason for rejected transaction")
    void getFailureReason_rejectedTransaction_returnsRejectReason() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);
        LocalDateTime completedAt = createdAt.plusHours(1);
        String rejectReason = "Invalid account number";

        transaction.reject(completedAt, rejectReason);

        assertEquals(rejectReason, transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should return null failure reason for pending transaction")
    void getFailureReason_pendingTransaction_returnsNull() {
        TransactionId transactionId = TransactionId.generate();
        Transaction transaction = Transaction.createDepositRecord(transactionId, depositAmount, targetAccountId, createdAt);

        assertNull(transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should reconstitute a completed transaction from persisted state")
    void reconstitute_restoresCompleteTransaction() {
        TransactionId id = TransactionId.generate();
        Currency cur = Currency.USD;
        Money amt = Money.of(BigDecimal.valueOf(250).setScale(2), cur);
        AccountId source = AccountId.of("444444444444444");
        AccountId target = AccountId.of("555555555555555");
        TransactionType type = TransactionType.TRANSFER;
        TransactionStatus status = TransactionStatus.COMPLETED;
        LocalDateTime created = LocalDateTime.of(2024, 6, 1, 8, 0);
        LocalDateTime completed = LocalDateTime.of(2024, 6, 1, 9, 30);

        Transaction transaction = Transaction.reconstitute(id, cur, amt, source, target, type, status, created, completed, null, null);

        assertEquals(id, transaction.getTransactionId());
        assertEquals(cur, transaction.getCurrency());
        assertEquals(amt, transaction.getAmount());
        assertEquals(source, transaction.getSourceAccount());
        assertEquals(target, transaction.getTargetAccount());
        assertEquals(type, transaction.getTransactionType());
        assertEquals(status, transaction.getTransactionStatus());
        assertEquals(created, transaction.getCreatedAt());
        assertEquals(completed, transaction.getCompletedAt());
    }

    @Test
    @DisplayName("Should reconstitute a returned transaction with return reason")
    void reconstitute_returnedTransaction_withReason() {
        TransactionId id = TransactionId.generate();
        Currency cur = Currency.EUR;
        Money amt = Money.of(BigDecimal.valueOf(100).setScale(2), cur);
        AccountId source = AccountId.of("666666666666666");
        TransactionType type = TransactionType.DEPOSIT;
        TransactionStatus status = TransactionStatus.RETURNED;
        LocalDateTime created = LocalDateTime.of(2024, 7, 15, 14, 0);
        LocalDateTime completed = LocalDateTime.of(2024, 7, 16, 10, 0);
        String returnReason = "Funds not available";

        Transaction transaction = Transaction.reconstitute(id, cur, amt, source, source, type, status, created, completed, returnReason, null);

        assertEquals(status, transaction.getTransactionStatus());
        assertTrue(transaction.isReturned());
        assertEquals(returnReason, transaction.getReturnReason());
        assertEquals(returnReason, transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should reconstitute a rejected transaction with reject reason")
    void reconstitute_rejectedTransaction_withRejectReason() {
        TransactionId id = TransactionId.generate();
        Currency cur = Currency.GBP;
        Money amt = Money.of(BigDecimal.valueOf(75).setScale(2), cur);
        AccountId source = AccountId.of("777777777777777");
        TransactionType type = TransactionType.WITHDRAW;
        TransactionStatus status = TransactionStatus.REJECTED;
        LocalDateTime created = LocalDateTime.of(2024, 9, 1, 16, 30);
        LocalDateTime completed = LocalDateTime.of(2024, 9, 2, 11, 15);
        String rejectReason = "Account frozen";

        Transaction transaction = Transaction.reconstitute(id, cur, amt, source, source, type, status, created, completed, null, rejectReason);

        assertEquals(status, transaction.getTransactionStatus());
        assertTrue(transaction.isRejected());
        assertEquals(rejectReason, transaction.getRejectReason());
    }
}
