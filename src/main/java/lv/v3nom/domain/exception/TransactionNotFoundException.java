package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.*;

import java.time.LocalDateTime;

public class TransactionNotFoundException extends RuntimeException {
    private final TransactionId transactionId;
    private final AccountId accountId;
    private final TransactionType transactionType;
    private final TransactionStatus transactionStatus;
    private final LocalDateTime createdAt;

    public TransactionNotFoundException() {
        super(String.format("Transaction not found"));
        this.transactionId = null;
        this.accountId = null;
        this.transactionType = null;
        this.transactionStatus = null;
        this.createdAt = null;
    }
    public TransactionNotFoundException(TransactionId transactionId) {
        super(String.format("Transaction not found. Transaction ID=[%s]", transactionId.getValue()));
        this.transactionId = transactionId;
        this.accountId = null;
        this.transactionType = null;
        this.transactionStatus = null;
        this.createdAt = null;
    }
    public TransactionNotFoundException(AccountId accountId) {
        super(String.format("Transaction not found. Account ID=[%s]", accountId.getValue()));
        this.transactionId = null;
        this.accountId = accountId;
        this.transactionType = null;
        this.transactionStatus = null;
        this.createdAt = null;
    }
    public TransactionNotFoundException(TransactionType transactionType) {
        super(String.format("Transaction not found. Type=[%s]", transactionType.getTransactionName()));
        this.transactionId = null;
        this.accountId = null;
        this.transactionType = transactionType;
        this.transactionStatus = null;
        this.createdAt = null;
    }
    public TransactionNotFoundException(TransactionStatus transactionStatus) {
        super(String.format("Transaction not found. Status=[%s]", transactionStatus.getValue()));
        this.transactionId = null;
        this.accountId = null;
        this.transactionType = null;
        this.transactionStatus = transactionStatus;
        this.createdAt = null;
    }
    public TransactionNotFoundException(LocalDateTime createdAt) {
        super(String.format("Transaction not found. Created At=[%s]", createdAt.toString()));
        this.transactionId = null;
        this.accountId = null;
        this.transactionType = null;
        this.transactionStatus = null;
        this.createdAt = createdAt;
    }

    public TransactionId getTransactionId() { return transactionId; }
    public AccountId getAccountId() { return accountId; }
    public TransactionType getTransactionType() { return transactionType; }
    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
