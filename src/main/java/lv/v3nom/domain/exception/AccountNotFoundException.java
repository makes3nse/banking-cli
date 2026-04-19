package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.AccountStatus;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.Money;

import java.time.LocalDateTime;

public class AccountNotFoundException extends RuntimeException {
    private final AccountId accountId;
    private final CustomerId ownerId;
    private final AccountStatus accountStatus;

    public AccountNotFoundException() {
        super("Account not found");
        this.accountId = null;
        this.ownerId = null;
        this.accountStatus = null;
    }
    public AccountNotFoundException(AccountId accountId) {
        super(String.format("Account not found. Account ID=[%s]", accountId.getValue()));
        this.accountId = accountId;
        this.ownerId = null;
        this.accountStatus = null;
    }
    public AccountNotFoundException(CustomerId ownerId) {
        super(String.format("Account not found. Customer ID=[%s]", ownerId.getValue()));
        this.accountId = null;
        this.ownerId = ownerId;
        this.accountStatus = null;
    }
    public AccountNotFoundException(AccountStatus accountStatus) {
        super(String.format("Account not found. Status=[%s]", accountStatus.getValue()));
        this.accountId = null;
        this.ownerId = null;
        this.accountStatus = accountStatus;
    }

    public AccountId getAccountId() { return accountId; }
    public CustomerId getOwnerId() { return ownerId; }
    public AccountStatus getAccountStatus() { return accountStatus; }
}
