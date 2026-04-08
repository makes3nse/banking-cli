package lv.v3nom.domain.value;

import java.util.regex.Pattern;

public class EmailAddress {
    private final String value;

    private EmailAddress(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be blank or null");
        }
        this.value = value;
    }

    public static EmailAddress of(String value) {
        String regex = "^.+@.+\\..+$";
        if (!Pattern.matches(regex, value)) {
            throw new IllegalArgumentException("Provided string is not an email");
        }
        return new EmailAddress(value);
    }

    public String getValue() {
        return value;
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
}
