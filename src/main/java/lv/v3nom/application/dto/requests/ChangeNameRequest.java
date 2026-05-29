package lv.v3nom.application.dto.requests;

public class ChangeNameRequest {
    private String currentSessionToken;
    private String idempotencyKey;
    private String currentName;
    private String newName;

    public ChangeNameRequest() {}
    public ChangeNameRequest(String currentSessionToken,
                             String idempotencyKey,
                             String currentName,
                             String newName) {

        this.currentSessionToken = currentSessionToken;
        this.idempotencyKey = idempotencyKey;
        this.currentName = currentName;
        this.newName = newName;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCurrentName() { return currentName; }
    public String getNewName() { return newName; }
}
