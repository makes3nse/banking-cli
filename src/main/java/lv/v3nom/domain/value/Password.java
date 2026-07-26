package lv.v3nom.domain.value;

import lv.v3nom.domain.security.PasswordHasher;

@SuppressWarnings("ClassCanBeRecord")
public  final class Password {
    private final String value; // hashed val

    private Password(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Reconstitution of provided Hashed Password: cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Reconstitution of provided Hashed Password: cannot be blank");
        }
        this.value = value;
    }

    public static Password fromRaw(String raw, PasswordHasher hasher) {
        if (raw == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        String hashed = hasher.hash(raw);
        return new Password(hashed);
    }
    public static Password of(String hashedPassword) {
        return new Password(hashedPassword);
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
