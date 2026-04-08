package lv.v3nom.domain.value;

public class PhoneNumber {
    private final String value;

    private PhoneNumber(String value) {
        String valueCleaned = value.trim();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank or null");
        }
        if (!(valueCleaned.matches(".*\\d.*"))) {
            throw new IllegalArgumentException("Phone number must contain digits");
        }
        if (valueCleaned.startsWith("+")) {
            if (valueCleaned.length() < 3) {
                throw new IllegalArgumentException("Phone number too short after +");
            }
            if (!valueCleaned.substring(1).matches("[\\d\\s\\-\\(\\)]+")) {
                throw new IllegalArgumentException("Invalid characters after +");
            }
        } else {
            if (!valueCleaned.matches("[\\d\\s\\-\\(\\)]+")) {
                throw new IllegalArgumentException("Phone number contains invalid characters");
            }
        }
        
        String digitsOnly = valueCleaned.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 5) {
            throw new IllegalArgumentException("Phone number too short (minimum 5 digits)");
        }
        if (digitsOnly.length() > 15) {
            throw new IllegalArgumentException("Phone number too long (maximum 15 digits for E.164)");
        }

        this.value = value;
    }

    public static PhoneNumber of(String existingNumber) {
        return new PhoneNumber(existingNumber);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
