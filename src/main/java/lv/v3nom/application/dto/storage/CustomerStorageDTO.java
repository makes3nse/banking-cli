package lv.v3nom.application.dto.storage;

import java.io.Serializable;

public class CustomerStorageDTO implements Serializable {
    //indirect Customer serialization
    private static final long serialVersionUID = 1L;

    private String id;
    private String role;
    private String customerStatus;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String createdAt;
    private String updatedAt;

    public CustomerStorageDTO(String id,
                              String role,
                              String customerStatus,
                              String name, String email,
                              String phoneNumber,
                              String password,
                              String createdAt,
                              String updatedAt) {

        this.id = id;
        this.role = role;
        this.customerStatus = customerStatus;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getRole() { return role; }
    public String getCustomerStatus() { return customerStatus; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
