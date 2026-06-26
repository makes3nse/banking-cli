package lv.v3nom.application.dto.requests;

public class SaveCachedResponseFromEmailRequest {
    private String email;
    private String idempotencyKey;
    private String responseJson;
    private String responseType;
    // Basically it's kind of never being used right now, but it's better
    // to have explicit type declaration to be able to validate things

    public SaveCachedResponseFromEmailRequest() {}
    public SaveCachedResponseFromEmailRequest(String email,
                                              String idempotencyKey,
                                              String responseJson,
                                              String responseType) {

        this.email = email;
        this.idempotencyKey = idempotencyKey;
        this.responseJson = responseJson;
        this.responseType = responseType;
    }

    public String getEmail() { return email; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getResponseJson() { return responseJson; }
    public String getResponseType() { return responseType; }
}
