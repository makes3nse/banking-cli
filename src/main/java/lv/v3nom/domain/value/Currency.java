package lv.v3nom.domain.value;

import java.util.Locale;
import java.util.Objects;

public record Currency(String value) {
    public Currency {
        Objects.requireNonNull(value, "Currency cannot be null");
        value = value.toUpperCase();
        if (!value.matches("[A-Z]{3}")) throw new IllegalArgumentException("Wrong currency format");
    }

    public static Currency of(String value) {
        return new Currency(value);
    }

    public static final Currency USD =
            new Currency("USD");
    public static final Currency EUR =
            new Currency("EUR");
    public static final Currency GBP =
            new Currency("GBP");

    public String getSymbol(Locale locale) {
        return java.util.Currency.getInstance(value).getSymbol(locale);
    }
    public int getDefaultFractionDigits() {
        return java.util.Currency.getInstance(value).getDefaultFractionDigits();
    }
}
