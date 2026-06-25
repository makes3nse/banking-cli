package lv.v3nom.application.dto.responses;

public class ChangeNameResponse {
    private String name;
    private String operationStatus;
    private String errorMessage;

    public ChangeNameResponse() {}
    public ChangeNameResponse(String name,
                              String operationStatus,
                              String errorMessage) {

        this.name = name;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getName() { return name; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
