package lv.v3nom.application.dto.responses;

public class CloseAccountResponse {
    private String currentSessionToken;
    private String idempotencyKey;
    private String operationStatus;
    private String errorMessage;

    public CloseAccountResponse() {}
    public CloseAccountResponse(String currentSessionToken,
                                String idempotencyKey,
                                String operationStatus,
                                String errorMessage) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
