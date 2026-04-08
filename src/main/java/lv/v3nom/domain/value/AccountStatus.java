package lv.v3nom.domain.value;

public class AccountStatus {
    private final String value;

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

    public static AccountStatus setValue(String newStatus) {
        return new AccountStatus(newStatus);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountStatus that = (AccountStatus) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
