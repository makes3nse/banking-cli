package lv.v3nom.application.dto.responses;

public class AuthResponse {
    private String customerId;

    public AuthResponse() {}
    public AuthResponse(String customerId) {

        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }
}
