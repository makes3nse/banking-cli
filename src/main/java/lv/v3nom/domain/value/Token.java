package lv.v3nom.domain.value;

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
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token value cannot be empty or null");
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
        if (value.matches("^[A-Za-z0-9_-]+$")) {
            throw new IllegalArgumentException("Token contains invalid characters");
        }
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
    @Override
    public String toString() {
        return String.format(
                "%s{expiry:%s, customerId:%s}",
                value, expiry, customerId
        );
    }

    public String getValue() { return this.value; }
    public LocalDateTime getExpiry() { return this.expiry; }
    public CustomerId getCustomerId() { return this.customerId; }
}
