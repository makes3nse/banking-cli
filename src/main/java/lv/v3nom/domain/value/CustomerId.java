package lv.v3nom.domain.value;

import lv.v3nom.common.util.IdGenerator;

@SuppressWarnings("ClassCanBeRecord")
public final class CustomerId {
    private final String value;

    private CustomerId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }
        this.value = value;
    }

    public static CustomerId generate() {
        return new CustomerId(IdGenerator.generateDigitId());
    }
    public static CustomerId of(String existingId) {
        return new CustomerId(existingId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    @Override
    public String toString() {
        return String.format(
                "CustomerId{value='%s'}",
                value
        );
    }

    public String getValue() { return this.value; }
}
