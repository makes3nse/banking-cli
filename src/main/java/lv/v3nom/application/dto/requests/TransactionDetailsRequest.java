package lv.v3nom.application.dto.requests;

public class TransactionDetailsRequest {
    private String currentSessionToken;
    private String accountId;
    private String transactionId;

    public TransactionDetailsRequest() {}
    public TransactionDetailsRequest(String currentSessionToken,
                                     String accountId,
                                     String transactionId) {

        this.currentSessionToken = currentSessionToken;
        this.accountId = accountId;
        this.transactionId = transactionId;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getAccountId() { return accountId; }
    public String getTransactionId() { return transactionId; }
}
