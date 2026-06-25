package lv.v3nom.application.dto.responses;

public class ChangePhoneNumberResponse {
    private String phone;
    private String operationStatus;
    private String errorMessage;

    public ChangePhoneNumberResponse() {}
    public ChangePhoneNumberResponse(String phone,
                                     String operationStatus,
                                     String errorMessage) {

        this.phone = phone;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getPhone() { return phone; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
