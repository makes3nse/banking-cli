package lv.v3nom.domain.exception;

import lv.v3nom.domain.value.CustomerId;

public class AuthenticationException extends RuntimeException {
    private final CustomerId customerId;

    public AuthenticationException(CustomerId customerId) {
        super(String.format("Authentication error for user %s", customerId.toString()));

        this.customerId = customerId;
    }

    public CustomerId getCustomerId() { return this.customerId; }
}
