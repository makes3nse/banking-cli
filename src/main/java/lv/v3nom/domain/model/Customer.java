package lv.v3nom.domain.model;

import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.EmailAddress;
import lv.v3nom.domain.value.PhoneNumber;
import lv.v3nom.infrastructure.util.IdGenerator;

import java.time.LocalDateTime;

public class Customer {
    private CustomerId id = CustomerId.generate();
    private String name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private LocalDateTime createdAt;

    public CustomerId getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public EmailAddress getEmail() {
        return email;
    }
    public void setEmail(EmailAddress email) {
        this.email = email;
    }
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
