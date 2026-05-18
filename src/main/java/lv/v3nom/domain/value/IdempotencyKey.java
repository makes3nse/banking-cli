package lv.v3nom.domain.value;

import lv.v3nom.common.rules.IdGenerationRules;

import java.util.UUID;

public final class IdempotencyKey {
    private final String value;

    private IdempotencyKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Idempotency Key cannot be null or blank");
        }
        if (!IdGenerationRules.UUID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid Idempotency Key format");
        }

        this.value = value;
    }

    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value);
    }

    public static IdempotencyKey generate() {
        return new IdempotencyKey(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        IdempotencyKey that = (IdempotencyKey) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() { return value; }
}
