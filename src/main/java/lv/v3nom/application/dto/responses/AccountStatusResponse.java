package lv.v3nom.application.dto.responses;

public class AccountStatusResponse {
    private String status;
    private String operationStatus;
    private String errorMessage;

    public AccountStatusResponse() {}
    public AccountStatusResponse(String status,
                                 String operationStatus,
                                 String errorMessage) {

        this.status = status;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getStatus() { return status; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
