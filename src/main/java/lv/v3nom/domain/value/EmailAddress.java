package lv.v3nom.domain.value;

import java.util.regex.Pattern;

@SuppressWarnings("ClassCanBeRecord")
public final class EmailAddress {
    private final String value;

    private EmailAddress(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be blank or null");
        }
        String regex = "^.+@.+\\..+$";
        if (!Pattern.matches(regex, value)) {
            throw new IllegalArgumentException("Provided string is not an email");
        }
        this.value = value;
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() { return value; }
}
