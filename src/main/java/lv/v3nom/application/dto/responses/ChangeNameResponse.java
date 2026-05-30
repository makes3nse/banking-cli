package lv.v3nom.application.dto.responses;

public class ChangeNameResponse {
    private String currentSessionToken;
    private String idempotencyKey;
    private String name;
    private String operationStatus;
    private String errorMessage;

    public ChangeNameResponse() {}
    public ChangeNameResponse(String currentSessionToken,
                              String idempotencyKey,
                              String name,
                              String operationStatus,
                              String errorMessage) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.name = name;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getName() { return name; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
