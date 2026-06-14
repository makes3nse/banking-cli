package lv.v3nom.application.dto.requests;

import java.math.BigDecimal;

public class CreateTransactionRequest {
    private String transactionType;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String createdAt;
    private String sourceAccountId;
    private String targetAccountId;

    public CreateTransactionRequest() {}

    public CreateTransactionRequest(String transactionType,
                                    String transactionId,
                                    BigDecimal amount,
                                    String currency,
                                    String createdAt,
                                    String sourceAccountId,
                                    String targetAccountId) {

        this.transactionType = transactionType;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    public String getTransactionType() { return transactionType; }
    public String getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCreatedAt() { return createdAt; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
}
