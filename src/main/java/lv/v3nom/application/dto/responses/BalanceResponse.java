package lv.v3nom.application.dto.responses;

import java.math.BigDecimal;

public class ViewBalanceResponse {
    private String accountId;
    private String status;
    private String currency;
    private BigDecimal balance;
    private String operationStatus;
    private String errorMessage;

    public ViewBalanceResponse() {}
    public ViewBalanceResponse(String accountId,
                               String status,
                               String currency,
                               BigDecimal balance,
                               String operationStatus,
                               String errorMessage) {

        this.accountId = accountId;
        this.status = status;
        this.currency = currency;
        this.balance = balance;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getAccountId() { return accountId; }
    public String getStatus() { return status; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
