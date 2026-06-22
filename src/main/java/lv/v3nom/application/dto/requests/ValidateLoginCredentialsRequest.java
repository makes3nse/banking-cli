package lv.v3nom.application.dto.requests;

public class ValidateLoginCredentialsRequest {
    private String customerId;
    private String rawPassword;
    private String emailAddress;

    public ValidateLoginCredentialsRequest() {}
    public ValidateLoginCredentialsRequest(String customerId,
                                           String rawPassword,
                                           String emailAddress) {

        this.customerId = customerId;
        this.rawPassword = rawPassword;
        this.emailAddress = emailAddress;
    }

    public String getCustomerId() { return customerId; }
    public String getRawPassword() { return rawPassword; }
    public String getEmailAddress() { return emailAddress; }
}
