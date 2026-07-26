package lv.v3nom.domain.value;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public final class TransactionStatus {
    private final String value;

    private static final Map<String, TransactionStatus> CACHE = new HashMap<>();

    private TransactionStatus(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Status cannot be blank");
        }

        this.value = value;
    }

    public static final TransactionStatus PENDING =
            new TransactionStatus("PENDING");
    public static final TransactionStatus COMPLETED =
            new TransactionStatus("COMPLETED");
    public static final TransactionStatus REJECTED =
            new TransactionStatus("REJECTED");
    public static final TransactionStatus RETURNED =
            new TransactionStatus("RETURNED");

    static {
        CACHE.put("PENDING", PENDING);
        CACHE.put("COMPLETED", COMPLETED);
        CACHE.put("REJECTED", REJECTED);
        CACHE.put("RETURNED", RETURNED);
    }

    public static TransactionStatus of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("of() -> TransactionStatus cannot be null");
        }

        TransactionStatus status = CACHE.get(value);
        if (status == null) {
            throw new IllegalArgumentException("of() -> Wrong Transaction status format: " + value);
        }
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        TransactionStatus that = (TransactionStatus) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    public String getValue() { return this.value; }
}
