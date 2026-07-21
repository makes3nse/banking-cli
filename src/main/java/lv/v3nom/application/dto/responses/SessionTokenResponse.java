package lv.v3nom.application.dto.responses;

public class SessionTokenResponse {
    private final String sessionToken;

    public SessionTokenResponse(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() { return sessionToken; }
}
