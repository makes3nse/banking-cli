package lv.v3nom.application.dto.responses;

public class ChangeEmailResponse {
    private String email;
    private String operationStatus;
    private String errorMessage;

    public ChangeEmailResponse() {}
    public ChangeEmailResponse(String email,
                               String operationStatus,
                               String errorMessage) {

        this.email = email;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getEmail() { return email; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
