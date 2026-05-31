package lv.v3nom.application.dto.requests;

public class GetAccountsRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String customerId;

    public GetAccountsRequest() {}
    public GetAccountsRequest(String currentSessionToken,
                              String idempotencyKey,
                              String customerId) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.customerId = customerId;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCustomerId() { return customerId; }
}
