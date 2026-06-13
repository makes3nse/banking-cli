package lv.v3nom.application.dto.requests;

public class ValidateTokenRequest {
    private String tokenValue;

    public ValidateTokenRequest() {}
    public ValidateTokenRequest(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getTokenValue() { return tokenValue; }
}
