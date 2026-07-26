package lv.v3nom.domain.value;

import jdk.jshell.execution.LocalExecutionControl;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class Token {
    private final String value;
    private final LocalDateTime expiry;
    private final CustomerId customerId;


    private Token(String value, LocalDateTime expiry, CustomerId customerId) {
        this.value = value;
        this.expiry = expiry;
        this.customerId = customerId;
    }

    public static Token create(String value, LocalDateTime expiry, CustomerId customerId, LocalDateTime now) {
        if (value == null) {
            throw new IllegalArgumentException("Token value cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Token value cannot be blank");
        }
        if (expiry == null) {
            throw new IllegalArgumentException("Expire date is null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerID is null");
        }
        if (expiry.isBefore(now)) {
            throw new IllegalArgumentException("Cannot create expired token");
        }
        if (value.length() < 32) {
            throw new IllegalArgumentException("Token is shorter than expected: " + value.length());
        }
        return new Token(value, expiry, customerId);
    }
    public static Token of(String value, LocalDateTime expiry, CustomerId customerId) {
        return new Token(value, expiry, customerId);
    }

    public boolean isValid(LocalDateTime now) {
        return this.expiry.isAfter(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token that = (Token) o;
        return this.value.equals(that.value) &&
                this.expiry.equals(that.expiry) &&
                this.customerId.equals(that.customerId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(value, expiry, customerId);
    }
    public String toStringFormatted() {
        return String.format(
                "%s{expiry:%s, customerId:%s}",
                value, expiry, customerId
        );
    }
    @Override
    public String toString() {
        return value;
    }

    public String getValue() { return this.value; }
    public LocalDateTime getExpiry() { return this.expiry; }
    public CustomerId getCustomerId() { return this.customerId; }
}
