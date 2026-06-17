package lv.v3nom.application.dto.requests;

public class RejectTransactionRequest {
    private String currentSessionToken;
    private String transactionId;
    private String role;
    private String rejectReason;

    public RejectTransactionRequest(String currentSessionToken,
                                    String transactionId,
                                    String role,
                                    String rejectReason) {

        this.currentSessionToken = currentSessionToken;
        this.transactionId = transactionId;
        this.role = role;
        this.rejectReason = rejectReason;
    }

    public String getRejectReason() { return rejectReason; }
    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getTransactionId() { return transactionId; }
    public String getRole() { return role; }
}
