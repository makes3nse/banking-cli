package lv.v3nom.application.dto.requests;

public class GetAccountsRequest {
    private String currentSessionToken;

    public GetAccountsRequest() {}
    public GetAccountsRequest(String currentSessionToken) {

        this.currentSessionToken = currentSessionToken;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
}
