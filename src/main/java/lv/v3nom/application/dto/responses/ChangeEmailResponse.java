package lv.v3nom.application.dto.responses;

public class ChangeEmailResponse {
    private String currentSessionToken;
    private String idempotencyKey;
    private String email;
    private String operationStatus;
    private String errorMessage;

    public ChangeEmailResponse() {}
    public ChangeEmailResponse(String currentSessionToken,
                               String idempotencyKey,
                               String email,
                               String operationStatus,
                               String errorMessage) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.email = email;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getEmail() { return email; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
