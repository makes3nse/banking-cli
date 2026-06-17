package lv.v3nom.application.dto.requests;

public class ViewCustomerRightsRequest {
    private String currentSessionToken;

    public ViewCustomerRightsRequest() {}
    public ViewCustomerRightsRequest(String currentSessionToken) {

        this.currentSessionToken = currentSessionToken;
    }

    public String getCurrentSessionToken() { return currentSessionToken; }
}
