package lv.v3nom.application.dto.requests;

public class GenerateSessionTokenRequest {
    private final String customerId;

    public GenerateSessionTokenRequest(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }
}
