package lv.v3nom.domain.value;

public final class OperationStatus {
    private final String value;
    private final boolean isOperational;
    private final boolean isStable;
    private final String description;

    private OperationStatus(String value,
                            boolean isOperational,
                            boolean isStable,
                            String description) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Operation Status cannot be null or empty");
        }

        this.value = value;
        this.isOperational = isOperational;
        this.isStable = isStable;
        this.description = description;
    }

    public static OperationStatus of(String value,
                                     boolean isOperational,
                                     boolean isStable,
                                     String description) {

        return new OperationStatus(value, isOperational, isStable, description);
    }

    public static final OperationStatus PROCESSING =
            new OperationStatus("PROCESSING", true, true, "");
    public static final OperationStatus SUCCESS =
            new OperationStatus("SUCCESS", true, true, "");
    public static final OperationStatus FAILURE =
            new OperationStatus("FAILURE", false, true, "");
    public static final OperationStatus UNKNOWN =
            new OperationStatus("UNKNOWN", true, false, "");

    public static OperationStatus of(String value) {
        switch (value) {
            case "PROCESSING":
                return OperationStatus.PROCESSING;
            case "SUCCESS":
                return OperationStatus.SUCCESS;
            case "FAILURE":
                return OperationStatus.FAILURE;
            case "UNKNOWN":
                return OperationStatus.UNKNOWN;
            default:
                throw new IllegalArgumentException("Wrong operation status format");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        OperationStatus that = (OperationStatus) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    @Override
    public String toString() {
        return String.format(value + ":" + description);
    }

    public String getValue() { return value; }
    public boolean isOperational() { return isOperational; }
    public String getDescription() { return description; }
}
