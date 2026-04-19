package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.CustomerStatus;
import lv.v3nom.domain.value.EmailAddress;
import lv.v3nom.domain.value.PhoneNumber;

import java.util.Optional;

public class CustomerNotFoundException extends RuntimeException {
    private final Optional<CustomerId> customerId;
    private final Optional<EmailAddress> emailAddress;
    private final Optional<PhoneNumber> phoneNumber;
    private final Optional<CustomerStatus> customerStatus;

    public CustomerNotFoundException(Optional<CustomerId> customerId,
                                     Optional<EmailAddress> emailAddress,
                                     Optional<PhoneNumber> phoneNumber,
                                     Optional<CustomerStatus> customerStatus) {
        super(buildMessage(customerId, emailAddress, phoneNumber, customerStatus));

        this.customerId = customerId;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.customerStatus = customerStatus;
    }

    private static String buildMessage(Optional<CustomerId> customerId,
                                Optional<EmailAddress> emailAddress,
                                Optional<PhoneNumber> phoneNumber,
                                Optional<CustomerStatus> customerStatus) {
        StringBuilder message = new StringBuilder("Customer not found with:");

        customerId.ifPresent(id -> message.append(" id=").append(id));
        emailAddress.ifPresent(email -> message.append(" email=").append(email));
        phoneNumber.ifPresent(phone -> message.append(" phone=").append(phone));
        customerStatus.ifPresent(status -> message.append(" status=").append(status));

        return message.toString();
    }

    public Optional<CustomerId> getCustomerId() { return customerId; }
    public Optional<EmailAddress> getEmailAddress() { return emailAddress; }
    public Optional<PhoneNumber> getPhoneNumber() { return phoneNumber; }
    public Optional<CustomerStatus> getCustomerStatus() { return customerStatus; }
}
