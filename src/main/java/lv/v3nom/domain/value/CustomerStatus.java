package lv.v3nom.domain.value;

import java.util.HashMap;
import java.util.Map;

public final class CustomerStatus {
    public final String value;

    private static final Map<String, CustomerStatus> CACHE = new HashMap<>();

    private CustomerStatus(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Status cannot be blank");
        }
        this.value = value;
    }

    public static final CustomerStatus PENDING_VERIFICATION =
            new CustomerStatus("PENDING_VERIFICATION");
    public static final CustomerStatus ACTIVE =
            new CustomerStatus("ACTIVE");
    public static final CustomerStatus SUSPENDED =
            new CustomerStatus("SUSPENDED");
    public static final CustomerStatus BANNED =
            new CustomerStatus("BANNED");
    public static final CustomerStatus CLOSED =
            new CustomerStatus("CLOSED");

    static {
        CACHE.put("PENDING_VERIFICATION", PENDING_VERIFICATION);
        CACHE.put("ACTIVE", ACTIVE);
        CACHE.put("SUSPENDED", SUSPENDED);
        CACHE.put("BANNED", BANNED);
        CACHE.put("CLOSED", CLOSED);
    }

    public static CustomerStatus of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("of() -> CustomerStatus cannot be null");
        }

        CustomerStatus status = CACHE.get(value);
        if (status == null) {
            throw new IllegalArgumentException("of() -> Wrong CustomerStatus format: " + value);
        }
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerStatus that = (CustomerStatus) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    public String getValue() { return this.value; }
}
