package lv.v3nom.domain.value;

import java.util.HashMap;
import java.util.Map;

import static lv.v3nom.domain.value.CustomerStatus.BANNED;
import static lv.v3nom.domain.value.CustomerStatus.SUSPENDED;

@SuppressWarnings("ClassCanBeRecord")
public final class AccountStatus {
    private final String value;

    private static final Map<String, AccountStatus> CACHE = new HashMap<>();

    private AccountStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account status must not be null or blank");
        }
        this.value = value.toUpperCase();
    }

    public static final AccountStatus PENDING_VERIFICATION =
            new AccountStatus("PENDING_VERIFICATION");
    public static final AccountStatus ACTIVE =
            new AccountStatus("ACTIVE");
    public static final AccountStatus CLOSED =
            new AccountStatus("CLOSED");
    public static final AccountStatus FROZEN =
            new AccountStatus("FROZEN");
    public static final AccountStatus BLOCKED =
            new AccountStatus("BLOCKED");

    static {
        CACHE.put("PENDING_VERIFICATION", PENDING_VERIFICATION);
        CACHE.put("ACTIVE", ACTIVE);
        CACHE.put("CLOSED", CLOSED);
        CACHE.put("FROZEN", FROZEN);
        CACHE.put("BLOCKED", BLOCKED);
    }

    public static AccountStatus of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("of() -> AccountStatus cannot be null");
        }

        AccountStatus status = CACHE.get(value);
        if (status == null) {
            throw new IllegalArgumentException("of() -> Wrong AccountStatus format: " + value);
        }
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountStatus that = (AccountStatus) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() { return this.value.hashCode(); }

    public String getValue() { return this.value; }
}
