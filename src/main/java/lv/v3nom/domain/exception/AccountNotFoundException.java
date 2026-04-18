package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.AccountId;

public class AccountNotFoundException extends RuntimeException {
    private final AccountId accountId;

    public AccountNotFoundException(AccountId accountId) {
        super(String.format("Account [%s] not found", accountId.getValue()));

        this.accountId = accountId;
    }

    public AccountId getAccountId() { return this.accountId; }
}
