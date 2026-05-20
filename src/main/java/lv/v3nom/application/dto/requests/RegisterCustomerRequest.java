package lv.v3nom.application.dto.requests;

public class RegisterCustomerRequest {
    private String idempotencyKey;
    private String name;
    private String email;
    private String phone;
    private String rawPassword;

    public RegisterCustomerRequest() {}
    public RegisterCustomerRequest(String idempotencyKey,
                                   String name,
                                   String email,
                                   String phone,
                                   String rawPassword) {

        this.idempotencyKey = idempotencyKey;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rawPassword = rawPassword;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRawPassword() { return rawPassword; }
}
