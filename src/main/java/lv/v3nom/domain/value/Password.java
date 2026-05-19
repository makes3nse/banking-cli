package lv.v3nom.domain.value;

import lv.v3nom.domain.security.PasswordHasher;

@SuppressWarnings("ClassCanBeRecord")
public  final class Password {
    private final String value; // hashed val

    private Password(String value) {
        this.value = value;
    }

    public static Password fromRaw(String raw, PasswordHasher hasher) {
        if (raw.isBlank() || raw == null) throw new IllegalArgumentException("Password cannot be blank or null");
        String hashed = hasher.hash(raw);
        return new Password(hashed);
    }
    public boolean matches(String raw, PasswordHasher hasher) {
        return hasher.matches(raw, this.value); // NOT THE PasswordHasher IMPLEMENTATION.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Password that = (Password) o;
        return this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    public String getValue() { return this.value; }
}
