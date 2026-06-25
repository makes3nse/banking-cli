package lv.v3nom.application.dto.responses;

public class FreezeAccountResponse {
    private String operationStatus;
    private String errorMessage;

    public FreezeAccountResponse() {}
    public FreezeAccountResponse(String operationStatus,
                                 String errorMessage) {

        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
