package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;

public class Account {
    private final AccountId accountId;
    private final CustomerId ownerId;
    private final Currency currency;
    private Money balance;
    private AccountStatus accountStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Account(AccountId accountId,
                    CustomerId ownerId,
                    Currency currency,
                    Money balance,
                    AccountStatus accountStatus,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {

        this.accountId = accountId;
        this.ownerId = ownerId;
        this.currency = currency;
        this.balance = balance;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Account creation
    public static Account open(CustomerId ownerId,
                               Currency currency,
                               CustomerStatus customerStatus,
                               LocalDateTime createdAndUpdatedAt) {

        SystemDateTimeProvider clock = new SystemDateTimeProvider();
        if (ownerId == null) {
            throw new IllegalArgumentException(
                    "Cannot open account. Customer is not specified");
        }
        if (customerStatus != CustomerStatus.ACTIVE) {
            throw new IllegalStateException("Customer account should be ACTIVE to open account");
        }
        return new Account(
                AccountId.generate(),
                ownerId,
                currency,
                Money.zero(currency),
                AccountStatus.ACTIVE,
                createdAndUpdatedAt,
                createdAndUpdatedAt);
    }
    public static Account reconstitute(AccountId accountId,
                                       CustomerId ownerId,
                                       Currency currency,
                                       Money balance,
                                       AccountStatus accountStatus,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {

        Account account = new Account(accountId, ownerId, currency, balance, accountStatus, createdAt, updatedAt);
        account.updatedAt = updatedAt;
        return account;
    }

    // Business logic
    public void deposit(Currency currency, Money amount, LocalDateTime updatedAt) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Deposits are disabled for " + accountStatus.getValue() + " account");
        }
        if (!this.currency.equals(currency)) {
            throw new IllegalStateException(
                    String.format("Cannot deposit %s currency to %s account", currency, this.currency)
            );
        }

        this.balance = this.balance.add(amount);
        this.updatedAt = updatedAt;
    }
    public void withdraw(Currency currency, Money amount, LocalDateTime updatedAt) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Withdraws are disabled for " + accountStatus.getValue() + " account");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalArgumentException("Insufficient funds. Balance: " + this.balance);
        }
        if (!this.currency.equals(currency)) {
            throw new IllegalStateException(
                    String.format("Cannot withdraw %s currency from %s account", currency, this.currency)
            );
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = updatedAt;
    }
    public void transferToAccount(Account target, Currency currency, Money amount, LocalDateTime updatedAt) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (this.accountId.equals(target.accountId)) {
            throw new IllegalArgumentException("Cannot send self");
        }
        if (this.accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot transfer, sender account inactive");
        }
        if (target.accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot transfer, receiver account inactive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalArgumentException("Cannot transfer, insufficient funds");
        }
        if (!currency.equals(target.getCurrency())) {
            throw new IllegalStateException(
                    String.format("Cannot transfer %s currency to %s account", currency, target.getCurrency())
            );
        }

        this.balance = this.balance.subtract(amount);
        target.balance = target.balance.add(amount);
        this.updatedAt = updatedAt;
        target.updatedAt = updatedAt;
    }

    // States
    public void close(LocalDateTime updatedAt) {
        if (this.balance.isPositive()) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        if (this.accountStatus == AccountStatus.BLOCKED || this.accountStatus == AccountStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException("Cannot close " + this.accountStatus.getValue() + " account");
        }

        this.accountStatus = AccountStatus.CLOSED;
        this.updatedAt = updatedAt;
    }
    public void freeze(LocalDateTime updatedAt) {
        if (this.accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot freeze " + this.accountStatus.getValue() + " account");
        }

        this.accountStatus = AccountStatus.FROZEN;
        this.updatedAt = updatedAt;
    }

    public AccountId getAccountId() { return accountId; }
    public CustomerId getOwnerId() { return ownerId; }
    public Currency getCurrency() { return currency; }
    public Money getBalance() { return balance; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}