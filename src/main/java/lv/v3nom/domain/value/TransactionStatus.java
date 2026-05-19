package lv.v3nom.domain.value;

@SuppressWarnings("ClassCanBeRecord")
public final class TransactionStatus {
    private final String value;

    private TransactionStatus(String value) {
        if (value.isBlank() || value == null) {
            throw new IllegalArgumentException("Status cannot be null");
        } else {
            this.value = value;
        }
    }

    public static final TransactionStatus PENDING =
            new TransactionStatus("PENDING");
    public static final TransactionStatus COMPLETED =
            new TransactionStatus("COMPLETE");
    public static final TransactionStatus REJECTED =
            new TransactionStatus("REJECTED");
    public static final TransactionStatus RETURNED =
            new TransactionStatus("RETURNED");

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
