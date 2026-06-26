package lv.v3nom.application.dto.requests;

public class SaveCachedResponseRequest {
    private String customerId;
    private String idempotencyKey;
    private String responseJson;
    private String responseType;
    // Basically it's kind of never being used right now, but it's better
    // to have explicit type declaration to be able to validate things

    public SaveCachedResponseRequest() {}
    public SaveCachedResponseRequest(String customerId,
                                     String idempotencyKey,
                                     String responseJson,
                                     String responseType) {

        this.customerId = customerId;
        this.idempotencyKey = idempotencyKey;
        this.responseJson = responseJson;
        this.responseType = responseType;
    }

    public String getCustomerId() { return customerId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getResponseJson() { return responseJson; }
    public String getResponseType() { return responseType; }
}
