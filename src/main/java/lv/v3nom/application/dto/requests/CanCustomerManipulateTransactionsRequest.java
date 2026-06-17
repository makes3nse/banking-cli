package lv.v3nom.application.dto.requests;

public class CanCustomerManipulateTransactionsRequest {
    private String currentSessionToken;

    public CanCustomerManipulateTransactionsRequest() {}
    public CanCustomerManipulateTransactionsRequest(String currentSessionToken) {

        this.currentSessionToken = currentSessionToken;
    }

    public String getCurrentSessionToken() { return currentSessionToken;}
}
