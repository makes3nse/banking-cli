package lv.v3nom.application.dto.responses;

import java.math.BigDecimal;

public class BalanceResponse {
    private String accountId;
    private String status;
    private BigDecimal balance;

    public BalanceResponse() {}
    public BalanceResponse(String accountId, String status, BigDecimal balance) {
        this.accountId = accountId;
        this.status = status;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public String getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
}
