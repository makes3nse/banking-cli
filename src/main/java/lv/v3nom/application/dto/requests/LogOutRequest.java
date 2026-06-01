package lv.v3nom.application.dto.requests;

public class LogOutRequest {
    private String currentSessionToken;
    private String idempotencyKey;

    public LogOutRequest() {}
    public LogOutRequest(String currentSessionToken,
                         String idempotencyKey) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
