package lv.v3nom.application.dto.requests;

public class TransactionHistoryRequest {
    private String currentSessionToken;
    private String accountId;
    private String fromDate;
    private String toDate;

    public TransactionHistoryRequest() {}
    public TransactionHistoryRequest(String currentSessionToken,
                                     String accountId,
                                     String fromDate,
                                     String toDate) {

        this.currentSessionToken = currentSessionToken;
        this.accountId = accountId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getAccountId() { return accountId; }
    public String getFromDate() { return fromDate; }
    public String getToDate() { return toDate; }
}
