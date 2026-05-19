package lv.v3nom.domain.value;

import lv.v3nom.common.util.IdGenerator;

@SuppressWarnings("ClassCanBeRecord")
public final class AccountId {
    private final String value;

    private AccountId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }
        this.value = value;
    }

    public static AccountId generate() {
        return new AccountId(IdGenerator.generateDigitId());
    }
    public static AccountId of(String existingId) {
        return new AccountId(existingId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountId that = (AccountId) o;
        return value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() { return this.value; }
}
