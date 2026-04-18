package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.CustomerId;

public class CustomerNotFoundException extends RuntimeException {
    private final CustomerId customerId;

    public CustomerNotFoundException(CustomerId customerId) {
        super(String.format("Customer %s not found", customerId));

        this.customerId = customerId;
    }

    public CustomerId getCustomerId() { return customerId; }
}
