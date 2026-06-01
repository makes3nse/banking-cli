package lv.v3nom.application.dto.requests;

public class GetCustomerRequest {
    private String currentSessionToken;

    public GetCustomerRequest() {}
    public GetCustomerRequest(String currentSessionToken) {

        this.currentSessionToken = currentSessionToken;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
}
