package lv.v3nom.application.dto.requests;

public class GetAccountsRequest {
    private String currentSessionToken;
    private String customerId;

    public GetAccountsRequest() {}
    public GetAccountsRequest(String currentSessionToken,
                              String customerId) {

        this.currentSessionToken = currentSessionToken;
        this.customerId = customerId;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
    public String getCustomerId() { return customerId; }
}
