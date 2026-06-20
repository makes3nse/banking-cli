package lv.v3nom.application.dto.requests;

public class GetCustomerByEmailRequest {
    private String email;

    public GetCustomerByEmailRequest() {} // for possible frameworks
    public GetCustomerByEmailRequest(String email) {

        this.email = email;
    }

    public String getEmail() { return email; }
}
