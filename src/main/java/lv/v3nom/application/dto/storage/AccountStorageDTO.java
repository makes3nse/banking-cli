package lv.v3nom.application.dto.storage;

import java.io.Serializable;

public class AccountStorageDTO implements Serializable {
    //indirect Account serialization
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String ownerId;
    private String currency;
    private String balance;
    private String accountStatus;
    private String createdAt;
    private String updatedAt;

    public AccountStorageDTO(String accountId,
                             String ownerId,
                             String currency,
                             String balance,
                             String accountStatus,
                             String createdAt,
                             String updatedAt) {

        this.accountId = accountId;
        this.ownerId = ownerId;
        this.currency = currency;
        this.balance = balance;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getAccountId() { return accountId; }
    public String getOwnerId() { return ownerId; }
    public String getCurrency() { return currency; }
    public String getBalance() { return balance; }
    public String getAccountStatus() { return accountStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
