package lv.v3nom.application.dto.requests;

public class CompleteTransactionRequest {
    private String transactionId;
    private String completedAt;

    public CompleteTransactionRequest() {}

    public CompleteTransactionRequest(String transactionId, String completedAt) {
        this.transactionId = transactionId;
        this.completedAt = completedAt;
    }

    public String getTransactionId() { return transactionId; }
    public String getCompletedAt() { return completedAt; }
}
