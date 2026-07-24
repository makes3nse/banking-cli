package lv.v3nom.application.dto.storage;

import java.io.Serializable;

public class TokenStorageDTO implements Serializable {
    //indirect Token serialization
    private static final long serialVersionUID = 1L;

    private String value;
    private String expiry;
    private String customerId;

    public TokenStorageDTO(String value,
                              String expiry,
                              String customerId) {
        this.value = value;
        this.expiry = expiry;
        this.customerId = customerId;
    }

    public String getValue() { return value; }
    public String getExpiry() { return expiry; }
    public String getCustomerId() { return customerId; }
}
