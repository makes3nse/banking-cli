package lv.v3nom.application.dto.requests;

public class ChangeEmailRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String currentEmail;
    private String newEmail;

    public ChangeEmailRequest() {}
    public ChangeEmailRequest(String currentSessionToken,
                              String idempotencyKey,
                              String currentEmail,
                              String newEmail) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.currentEmail = currentEmail;
        this.newEmail = newEmail;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCurrentEmail() { return currentEmail; }
    public String getNewEmail() { return newEmail; }
}
