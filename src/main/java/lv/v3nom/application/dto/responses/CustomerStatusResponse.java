package lv.v3nom.application.dto.responses;

import java.math.BigDecimal;

public class CustomerStatusResponse {
    private String status;
    private String operationStatus;
    private String errorMessage;

    public CustomerStatusResponse() {}
    public CustomerStatusResponse(String customerId,
                                  String status,
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
