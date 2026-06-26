package lv.v3nom.application.dto.requests;

public class GetCachedResponseFromEmailRequest {
    private String email;
    private String idempotencyKey;

    public GetCachedResponseFromEmailRequest(String email, String idempotencyKey) {
        this.email = email;
        this.idempotencyKey = idempotencyKey;
    }

    public String getEmail() { return email; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
