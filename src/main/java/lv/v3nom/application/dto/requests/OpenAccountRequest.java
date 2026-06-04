package lv.v3nom.application.dto.requests;

public class OpenAccountRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String customerId;
    private String currency;

    public OpenAccountRequest() {}
    public OpenAccountRequest(String currentSessionToken,
                              String idempotencyKey,
                              String customerId,
                              String currency) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.customerId = customerId;
        this.currency = currency;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCustomerId() { return  customerId; }
    public String getCurrency() { return currency; }
}
