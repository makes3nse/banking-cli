package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.util.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private SystemDateTimeProvider clock = new SystemDateTimeProvider();

    private AccountId accountId;
    private CustomerId ownerId;
    private Money balance;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;

    private List<TransactionId> transactionHistory;

    public Account(CustomerId ownerId) {
        this.accountId = AccountId.generate();
        this.ownerId = ownerId;
        this.balance = Money.ZERO;
        this.accountStatus = AccountStatus.ACTIVE;
        this.createdAt = clock.now();
        this.transactionHistory = new ArrayList<>();
    }

    public void deposit(Money amount) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Deposits are disabled for your account");
        }
        this.balance = this.balance.add(amount);
    }

    public AccountId getAccountId() { return accountId; }
    public CustomerId getOwnerId() { return ownerId; }
    public Money getBalance() { return balance; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<TransactionId> getTransactionHistory() { return transactionHistory; }
}