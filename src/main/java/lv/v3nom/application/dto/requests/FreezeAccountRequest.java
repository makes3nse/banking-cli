package lv.v3nom.application.dto.requests;

public class FreezeAccountRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String accountId;

    public FreezeAccountRequest() {}
    public FreezeAccountRequest(String currentSessionToken,
                                String idempotencyKey,
                                String accountId) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.accountId = accountId;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getAccountId() { return accountId; }
}
