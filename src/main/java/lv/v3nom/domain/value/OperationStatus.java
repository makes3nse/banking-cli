package lv.v3nom.domain.value;

import java.util.HashMap;
import java.util.Map;

public final class OperationStatus {
    private final String value;
    private final boolean isOperational;
    private final boolean isStable;
    private final String description;

    private static final Map<String, OperationStatus> CACHE = new HashMap<>();

    private OperationStatus(String value,
                            boolean isOperational,
                            boolean isStable,
                            String description) {

        if (value == null) {
            throw new IllegalArgumentException("Operation Status cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Operation Status cannot be blank");
        }

        this.value = value;
        this.isOperational = isOperational;
        this.isStable = isStable;
        this.description = description;
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
        if (value == null) return OperationStatus.failure("of() received 'null' as OperationStatus value");

        switch (value) {
            case "PROCESSING": return OperationStatus.processing();
            case "SUCCESS": return OperationStatus.success();
            case "FAILURE": return OperationStatus.failure(value);
            case "UNKNOWN": return OperationStatus.unknown();
            default: return OperationStatus.failure("of() received INVALID FORMAT as OperationStatus value");
        }
    }
    public static OperationStatus processing() {
        return new OperationStatus("PROCESSING", true, true, "");
    }
    public static OperationStatus success() {
        return new OperationStatus("SUCCESS", true, true, "");
    }
    public static OperationStatus failure(String message) {
        return new OperationStatus("FAILURE", false, true, message);
    }
    public static OperationStatus unknown() {
        return new OperationStatus("UNKNOWN", true, false, "");
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
