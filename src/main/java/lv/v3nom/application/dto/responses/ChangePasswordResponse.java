package lv.v3nom.application.dto.responses;

public class ChangePasswordResponse {
    private String operationStatus;
    private String errorMessage;

    public ChangePasswordResponse() {}
    public ChangePasswordResponse(String operationStatus,
                                  String errorMessage) {

        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
