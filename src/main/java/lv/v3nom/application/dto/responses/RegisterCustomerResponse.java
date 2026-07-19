package lv.v3nom.application.dto.responses;

public class RegisterCustomerResponse {
    private String customerId;
    private String sessionToken;
    private String name;
    private String email;
    private String phone;
    private String status;
    private String operationStatus;
    private String errorMessage;

    public RegisterCustomerResponse() {}
    public RegisterCustomerResponse(String customerId,
                                    String sessionToken,
                                    String name,
                                    String email,
                                    String phone,
                                    String status,
                                    String operationStatus,
                                    String errorMessage) {

        this.customerId = customerId;
        this.sessionToken = sessionToken;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public String getCustomerId() { return customerId; }
    public String getSessionToken() { return sessionToken; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
