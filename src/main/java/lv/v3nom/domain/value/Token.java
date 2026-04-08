package lv.v3nom.domain.value;

import lv.v3nom.infrastructure.util.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;
import java.util.Objects;

public class Token {
    SystemDateTimeProvider clock = new SystemDateTimeProvider();

    private final String value;
    private final LocalDateTime expiry;
    private final CustomerId customerId;


    private Token(String value, LocalDateTime expiry, CustomerId customerId) {
        this.value = value;
        this.expiry = expiry;
        this.customerId = customerId;
    }

    public static Token create(String value, LocalDateTime expiry, CustomerId customerId) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UUID cannot be empty or null");
        }
        if (expiry == null) {
            throw new IllegalArgumentException("Expire date is null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerID is null");
        }
        if (expiry.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create expired token");
        }
        return new Token(value, expiry, customerId);
    }

    public String getValue() {
        return this.value;
    }
    public LocalDateTime getExpiry() {
        return this.expiry;
    }
    public CustomerId getCustomerId() {
        return this.customerId;
    }
    public boolean isValid() {
        return clock.now().isBefore(expiry);
    }

    @Override
    public String toString() {
        return String.format(
                "token{expiry:%s, customerId:%s}",
                expiry, customerId
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this == null || getClass() != o.getClass()) return false;
        Token that = (Token) o;
        return this.value.equals(that.value) &&
                this.expiry.equals(that.expiry) &&
                this.customerId.equals(that.customerId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(value, expiry, customerId);
    }
}
