package lv.v3nom.application.dto.responses;

import java.math.BigDecimal;

public class BalanceResponse {
    private String accountId;
    private String currency;
    private BigDecimal balance;
    private String operationStatus;
    private String errorMessage;

    public BalanceResponse() {}
    public BalanceResponse(String accountId,
                           String currency,
                           BigDecimal balance,
                           String operationStatus,
                           String errorMessage) {

        this.accountId = accountId;
        this.currency = currency;
        this.balance = balance;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getAccountId() { return accountId; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
