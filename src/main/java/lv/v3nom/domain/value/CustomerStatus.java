package lv.v3nom.domain.value;

public final class CustomerStatus {
    public final String value;

    private CustomerStatus(String value) {
        if (value.isBlank() || value == null) {
            throw new IllegalArgumentException("Status cannot be blank or null");
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

    public CustomerStatus of(String value) {
        if (value != CustomerStatus.PENDING_VERIFICATION.value
                || value != CustomerStatus.ACTIVE.value
                || value != CustomerStatus.SUSPENDED.value
                || value != CustomerStatus.BANNED.value
                || value != CustomerStatus.CLOSED.value) {

            throw new IllegalArgumentException("Wrong account status format");
        }

        return new CustomerStatus(value);
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
