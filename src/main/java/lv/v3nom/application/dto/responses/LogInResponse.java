package lv.v3nom.application.dto.responses;

public class AuthResponse {
    private String customerId;
    private String sessionToken;
    private boolean success;
    private String status;
    private String name;
    private String operationStatus;
    private String errorMessage;

    public AuthResponse() {}
    public AuthResponse(String customerId,
                        String sessionToken,
                        boolean success,
                        String status,
                        String name,
                        String operationStatus,
                        String errorMessage) {

        this.customerId = customerId;
        this.sessionToken = sessionToken;
        this.success = success;
        this.status = status;
        this.name = name;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCustomerId() { return customerId; }
    public String getSessionToken() { return sessionToken; }
    public String getStatus() { return status; }
    public String getName() { return name; }
    public boolean isSuccess() { return success; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
