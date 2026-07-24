package lv.v3nom.domain.model;

import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.*;

import java.time.LocalDateTime;

public class Customer {
    private final CustomerId id;
    private Role role;
    private CustomerStatus customerStatus;
    private String name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private Password password;
    private transient PasswordHasher hasher;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Customer(CustomerId id,
                     Role role,
                     CustomerStatus customerStatus,
                     String name,
                     EmailAddress email,
                     PhoneNumber phoneNumber,
                     Password password,
                     PasswordHasher hasher,
                     LocalDateTime createdAt) {

        this.id = id;
        this.role = role;
        this.customerStatus = customerStatus;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.hasher = hasher;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static Customer register(String name,
                                    String email,
                                    String phone,
                                    String rawPassword,
                                    PasswordHasher hasher,
                                    LocalDateTime createdAt) {

        try {
            return new Customer(
                    CustomerId.generate(),
                    Role.CUSTOMER,
                    CustomerStatus.ACTIVE,
                    name.trim(),
                    EmailAddress.of(email),
                    PhoneNumber.of(phone),
                    Password.fromRaw(rawPassword, hasher),
                    hasher,
                    createdAt);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public static Customer reconstitute(CustomerId id,
                                       Role role,
                                       CustomerStatus customerStatus,
                                       String name,
                                       EmailAddress email,
                                       PhoneNumber phoneNumber,
                                       Password existingPassword,
                                       PasswordHasher hasher,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {

        Customer customer = new Customer(
                id, role, customerStatus, name, email, phoneNumber, existingPassword, hasher, createdAt);
        customer.updatedAt = updatedAt;
        return customer;
    }
    public void changeName(String newName) {
        if (this.customerStatus != CustomerStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Name change available only for ACTIVE customer accounts");
        }
        if (newName.isBlank() || newName == null) {
            throw new IllegalArgumentException(
                    "New customer name cannot be blank or null");
        }
        if (newName.length() < 3 || newName.length() > 45) {
            throw new IllegalArgumentException(
                    "Exceptional name length: " + newName.length()
                            + String.format(". Allowed name length from %s to %s characters.", 3, 45));
        }
        this.name = newName;
    }
    public void changeEmail(EmailAddress newEmail) {
        if (this.customerStatus != CustomerStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Email change available only for ACTIVE customer accounts");
        }
        if (newEmail.equals(this.email)) {
            throw new IllegalArgumentException("New email cannot be same");
        }
        this.email = newEmail;
    }
    public void changePhoneNumber(PhoneNumber newPhoneNumber) {
        if (this.customerStatus != CustomerStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Phone number change available only for ACTIVE customer accounts");
        }
        if (newPhoneNumber.equals(this.phoneNumber)) {
            throw new IllegalArgumentException("New phone number cannot be same");
        }
        this.phoneNumber = newPhoneNumber;
    }
    public void changePassword(String oldPwd, String newPwd, PasswordHasher hasher) {
        //check if old pwd is bad
        if (!this.password.matches(oldPwd, hasher)) {
            throw new IllegalStateException("Old password is incorrect.");
        }
        //check if they are the same
        if (this.password.matches(newPwd, hasher)) {
            throw new IllegalStateException("New password cannot be identical to old password.");
        }
        this.password = Password.fromRaw(newPwd, hasher);
    }

    public boolean canOpenAccount() {
        return this.customerStatus == CustomerStatus.ACTIVE;
    }

    public CustomerId getId() { return id; }
    public Role getRole() { return role; }
    public CustomerStatus getCustomerStatus() { return customerStatus; }
    public String getName() { return name; }
    public EmailAddress getEmail() { return email; }
    public PhoneNumber getPhoneNumber() { return phoneNumber; }
    public Password getPassword() { return password; }
    public PasswordHasher getHasher() { return hasher; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
