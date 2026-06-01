package lv.v3nom.application.dto.requests;

public class GetCustomerRequest {
    private String currentSessionToken;
    private String idempotencyKey;

    public GetCustomerRequest() {}
    public GetCustomerRequest(String currentSessionToken,
                              String idempotencyKey) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
    public String getIdempotencyKey() { return idempotencyKey; }
}
