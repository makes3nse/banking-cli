package lv.v3nom.application.dto.requests;

public class GetCachedResponseFromIdRequest {
    private String customerId;
    private String idempotencyKey;

    public GetCachedResponseFromIdRequest(String customerId, String idempotencyKey) {
        this.customerId = customerId;
        this.idempotencyKey = idempotencyKey;
    }

    public String getCustomerId() { return customerId; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
