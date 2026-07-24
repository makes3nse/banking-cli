package lv.v3nom.application.dto.storage;

import java.io.Serializable;

public class TransactionStorageDTO implements Serializable {
    //indirect Transaction serialization
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String currency;
    private String amount;
    private String sourceAccount;
    private String targetAccount;
    private String transactionType;
    private String transactionStatus;
    private String createdAt;
    private String completedAt;
    private String returnReason;
    private String rejectReason;

    public TransactionStorageDTO(String transactionId,
                                 String currency,
                                 String amount,
                                 String sourceAccount,
                                 String targetAccount,
                                 String transactionType,
                                 String transactionStatus,
                                 String createdAt,
                                 String completedAt,
                                 String returnReason,
                                 String rejectReason) {

        this.transactionId = transactionId;
        this.currency = currency;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.returnReason = returnReason;
        this.rejectReason = rejectReason;
    }

    public String getTransactionId() { return transactionId; }
    public String getCurrency() { return currency; }
    public String getAmount() { return amount; }
    public String getSourceAccount() { return sourceAccount; }
    public String getTargetAccount() { return targetAccount; }
    public String getTransactionType() { return transactionType; }
    public String getTransactionStatus() { return transactionStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getCompletedAt() { return completedAt; }
    public String getReturnReason() { return returnReason; }
    public String getRejectReason() { return rejectReason; }
}
