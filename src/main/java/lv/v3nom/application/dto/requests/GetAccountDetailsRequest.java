package lv.v3nom.application.dto.requests;

public class GetAccountDetailsRequest {
    private String currentSessionToken;
    private String accountId;

    public GetAccountDetailsRequest() {}
    public GetAccountDetailsRequest(String currentSessionToken,
                                    String accountId) {

        this.currentSessionToken = currentSessionToken;
        this.accountId = accountId;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getAccountId() { return accountId; }
}
