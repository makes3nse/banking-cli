package lv.v3nom.application.dto.responses;

public class LogInResponse {
    private String customerId;
    private String sessionToken;
    private String status;
    private String name;
    private String operationStatus;
    private boolean isSucceeded;
    private String errorMessage;

    public LogInResponse() {}
    public LogInResponse(String customerId,
                         String sessionToken,
                         String status,
                         String name,
                         String operationStatus,
                         boolean isSucceeded,
                         String errorMessage) {

        this.customerId = customerId;
        this.sessionToken = sessionToken;
        this.status = status;
        this.name = name;
        this.operationStatus = operationStatus;
        this.isSucceeded = isSucceeded;
        this.errorMessage = errorMessage;
    }

    public String getCustomerId() { return customerId; }
    public String getSessionToken() { return sessionToken; }
    public String getStatus() { return status; }
    public String getName() { return name; }
    public boolean isSucceeded() { return isSucceeded; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
