package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.CustomerStatus;
import lv.v3nom.domain.value.EmailAddress;
import lv.v3nom.domain.value.PhoneNumber;

import java.util.Optional;

public class CustomerNotFoundException extends RuntimeException {
    private final CustomerId customerId;
    private final EmailAddress emailAddress;
    private final PhoneNumber phoneNumber;
    private final CustomerStatus customerStatus;

    public CustomerNotFoundException() {
        super("Customers not found");

        this.customerId = null;
        this.emailAddress = null;
        this.phoneNumber = null;
        this.customerStatus = null;
    }
    public CustomerNotFoundException(CustomerId customerId) {
        super("Customer not found with id=" + customerId.getValue());

        this.customerId = customerId;
        this.emailAddress = null;
        this.phoneNumber = null;
        this.customerStatus = null;
    }
    public CustomerNotFoundException(EmailAddress emailAddress) {
        super("Customer not found with email=" + emailAddress.getValue());

        this.customerId = null;
        this.emailAddress = emailAddress;
        this.phoneNumber = null;
        this.customerStatus = null;
    }
    public CustomerNotFoundException(PhoneNumber phoneNumber) {
        super("Customer not found with id=" + phoneNumber.getValue());

        this.customerId = null;
        this.emailAddress = null;
        this.phoneNumber = phoneNumber;
        this.customerStatus = null;
    }
    public CustomerNotFoundException(CustomerStatus customerStatus) {
        super("Customer not found with id=" + customerStatus.getValue());

        this.customerId = null;
        this.emailAddress = null;
        this.phoneNumber = null;
        this.customerStatus = customerStatus;
    }

    public CustomerId getCustomerId() { return customerId; }
    public EmailAddress getEmailAddress() { return emailAddress; }
    public PhoneNumber getPhoneNumber() { return phoneNumber; }
    public CustomerStatus getCustomerStatus() { return customerStatus; }
}
