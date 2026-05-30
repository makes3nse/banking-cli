package lv.v3nom.application.dto.responses;

public class ChangePhoneNumberResponse {
    private String currentSessionToken;
    private String idempotencyKey;
    private String phone;
    private String operationStatus;
    private String errorMessage;

    public ChangePhoneNumberResponse() {}
    public ChangePhoneNumberResponse(String currentSessionToken,
                                     String idempotencyKey,
                                     String phone,
                                     String operationStatus,
                                     String errorMessage) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.phone = phone;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getPhone() { return phone; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
