package lv.v3nom.domain.model;

import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.Money;
import lv.v3nom.domain.value.TransactionId;
import lv.v3nom.domain.value.TransactionType;

public class Transaction {
    private TransactionId transactionId = TransactionId.generate();
    private Money amount;
    private AccountId fromAccount;
    private AccountId toAccount;
    private TransactionType transactionType;

    public TransactionId getTransactionId() {
        return transactionId;
    }
    public Money getAmount() {
        return amount;
    }
    public void setAmount(Money amount) {
        this.amount = amount;
    }
    public AccountId getFromAccount() {
        return fromAccount;
    }
    public void setFromAccount(AccountId fromAccount) {
        this.fromAccount = fromAccount;
    }
    public AccountId getToAccount() {
        return toAccount;
    }
    public void setToAccount(AccountId toAccount) {
        this.toAccount = toAccount;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
