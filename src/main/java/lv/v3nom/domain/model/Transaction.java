package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.util.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;

public class Transaction {
    private final TransactionId transactionId;
    private final Money amount;
    private final AccountId sourceAccount;
    private final AccountId targetAccount;
    private final TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String returnReason;
    private String rejectReason;

    private Transaction(TransactionId transactionId,
                       Money amount,
                       AccountId sourceAccount,
                       AccountId targetAccount,
                       TransactionType transactionType,
                       TransactionStatus transactionStatus,
                       LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
    }

    // newborn transaction states
    public static Transaction createDepositRecord(TransactionId transactionId,
                                                  Money amount,
                                                  AccountId targetAccount,
                                                  LocalDateTime createdAt) {
        return new Transaction(
                transactionId,
                amount,
                targetAccount,
                targetAccount,
                TransactionType.DEPOSIT,
                TransactionStatus.PENDING,
                createdAt);
    }
    public static Transaction createWithdrawalRecord(TransactionId transactionId,
                                                     Money amount,
                                                     AccountId sourceAccount,
                                                     LocalDateTime createdAt) {
        return new Transaction(
                transactionId,
                amount,
                sourceAccount,
                sourceAccount,
                TransactionType.WITHDRAW,
                TransactionStatus.PENDING,
                createdAt);
    }
    public static Transaction createTransferRecord(TransactionId transactionId,
                                                   Money amount,
                                                   AccountId sourceAccount,
                                                   AccountId targetAccount,
                                                   LocalDateTime createdAt) {
        return new Transaction(
                transactionId,
                amount,
                sourceAccount,
                targetAccount,
                TransactionType.TRANSFER,
                TransactionStatus.PENDING,
                createdAt);
    }

    // final transaction states
    public void completeTran() {
        if (this.transactionStatus != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot complete. Current transaction status is " + this.transactionStatus);
        }
        this.transactionStatus = TransactionStatus.COMPLETED;
        this.completedAt = new SystemDateTimeProvider().now();
    }
    public void returnTran(String returnReason) {
        if (this.transactionStatus == TransactionStatus.REJECTED ||
                this.transactionStatus == TransactionStatus.RETURNED) {
            throw new IllegalStateException("Transaction is already " + this.transactionStatus);
        }
        this.transactionStatus = TransactionStatus.RETURNED;
        this.returnReason = returnReason;
        this.completedAt = new SystemDateTimeProvider().now();
    }
    public void rejectTran(String rejectReason) {
        if (this.transactionStatus == TransactionStatus.REJECTED ||
                this.transactionStatus == TransactionStatus.RETURNED) {
            throw new IllegalStateException("Transaction is already " + this.transactionStatus);
        }
        this.transactionStatus = TransactionStatus.REJECTED;
        this.rejectReason = rejectReason;
        this.completedAt = new SystemDateTimeProvider().now();
    }

    // query
    public boolean isPending() {
        return this.transactionStatus == TransactionStatus.PENDING;
    }
    public boolean isComplete() {
        return this.transactionStatus == TransactionStatus.COMPLETED;
    }
    public boolean isReturned() {
        return this.transactionStatus == TransactionStatus.RETURNED;
    }
    public boolean isRejected() {
        return this.transactionStatus == TransactionStatus.REJECTED;
    }

    public TransactionId getTransactionId() { return transactionId; }
    public Money getAmount() { return amount; }
    public AccountId getSourceAccount() { return sourceAccount; }
    public AccountId getTargetAccount() { return targetAccount; }
    public TransactionType getTransactionType() { return transactionType; }
    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getReturnReason() { return returnReason; }
    public String getRejectReason() { return rejectReason; }
}
