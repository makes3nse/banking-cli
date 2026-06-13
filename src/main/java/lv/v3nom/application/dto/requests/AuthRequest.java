package lv.v3nom.application.dto.requests;

public class AuthRequest {
    private String tokenValue;

    public AuthRequest() {} // for possible frameworks
    public AuthRequest(String tokenValue) {

        this.tokenValue = tokenValue;
    }

    public String getTokenValue() { return tokenValue; }
}
