package lv.v3nom.application.dto.requests;

public class LogInRequest {
    private String idempotencyKey;
    private String email;
    private String password;

    public LogInRequest() {} // for possible frameworks
    public LogInRequest(String idempotencyKey,
                        String email,
                        String password) {

        this.idempotencyKey = idempotencyKey;
        this.email = email;
        this.password = password;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // dtos r immutable serializable simple objs, no setters (or return void)
}
