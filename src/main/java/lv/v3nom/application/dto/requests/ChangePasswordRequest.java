package lv.v3nom.application.dto.requests;

public class ChangePasswordRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String currentPassword;
    private String newPassword;

    public ChangePasswordRequest() {}
    public ChangePasswordRequest(String currentSessionToken,
                                 String idempotencyKey,
                                 String currentPassword,
                                 String newPassword) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
}
