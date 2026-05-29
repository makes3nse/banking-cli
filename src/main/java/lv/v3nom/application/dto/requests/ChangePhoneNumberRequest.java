package lv.v3nom.application.dto.requests;

public class ChangePhoneNumberRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String currentPhone;
    private String newPhone;

    public ChangePhoneNumberRequest() {}
    public ChangePhoneNumberRequest(String currentSessionToken,
                                    String idempotencyKey,
                                    String currentPhone,
                                    String newPhone) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.currentPhone = currentPhone;
        this.newPhone = newPhone;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCurrentPhone() { return currentPhone; }
    public String getNewPhone() { return newPhone; }
}
