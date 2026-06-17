package lv.v3nom.application.dto.requests;

public class ReturnTransactionRequest {
    private String currentSessionToken;
    private String transactionId;
    private String role;
    private String returnReason;

    public ReturnTransactionRequest(String currentSessionToken,
                                    String transactionId,
                                    String role,
                                    String returnReason) {

        this.currentSessionToken = currentSessionToken;
        this.transactionId = transactionId;
        this.role = role;
        this.returnReason = returnReason;
    }

    public String getReturnReason() { return returnReason; }
    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getTransactionId() { return transactionId; }
    public String getRole() { return role; }
}
