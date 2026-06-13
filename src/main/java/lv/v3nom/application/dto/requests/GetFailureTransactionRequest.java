package lv.v3nom.application.dto.requests;

public class GetFailureTransactionRequest {
    private String transactionType;
    private String failureReason;
    private String operationStatus;

    public GetFailureTransactionRequest(String transactionType,
                                        String failureReason,
                                        String operationStatus) {

        this.transactionType = transactionType;
        this.failureReason = failureReason;
        this.operationStatus = operationStatus;
    }

    public String getTransactionType() { return transactionType; }
    public String getFailureReason() { return failureReason; }
    public String getOperationStatus() { return operationStatus; }
}
