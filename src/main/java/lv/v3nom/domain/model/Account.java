package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;

import java.time.LocalDateTime;
import java.util.List;

public class Account {
    private AccountId accountId = AccountId.generate();
    private CustomerId ownerId;
    private Money balance;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;

    private List<TransactionId> transactionHistory;

    public AccountId getAccountId() {
        return accountId;
    }
    public CustomerId getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(CustomerId ownerId) {
        this.ownerId = ownerId;
    }
    public Money getBalance() {
        return balance;
    }
    public void setBalance(Money balance) {
        this.balance = balance;
    }
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<TransactionId> getTransactionHistory() {
        return transactionHistory;
    }
    public void setTransactionHistory(List<TransactionId> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}