package lv.v3nom.application.dto.requests;

public class ViewBalanceRequest {
    private String currentSessionToken;
    private String accountId;

    public ViewBalanceRequest() {}
    public ViewBalanceRequest(String currentSessionToken,
                              String accountId) {

        this.currentSessionToken = currentSessionToken;
        this.accountId = accountId;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
    public String getAccountId() { return accountId; }
}
