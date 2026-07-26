package lv.v3nom.domain.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class Money implements Comparable<Money>{
    private final BigDecimal amount;
    private final Currency currency;

    //  public static final Money ZERO = new Money(BigDecimal.ZERO);

    private Money(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Money amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative" + amount);
        }

        int decimalPlaces = currency.getDefaultFractionDigits();
        BigDecimal roundedAmount = amount.setScale(decimalPlaces, RoundingMode.HALF_UP);

        this.amount = roundedAmount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }
    public static Money of(long amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // MATH
    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), other.currency);
    }
    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), other.currency);
    }
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }
    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalStateException(
                    String.format("Cannot operate in different currencies: %s, %s", this.currency, other.currency)
            );
        }
    }

    // COMPARISON
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }
    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }
    public boolean isGreaterThanOrEqualTo(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
    public boolean isNegativeOrZero() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    // HELPERS
    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money that = (Money) o;
        return this.amount.compareTo(that.amount) == 0 &&
                this.currency.equals(that.currency);
    }
    @Override
    public int hashCode() {
        return this.amount.hashCode();
    }
    @Override
    public String toString() {
        return this.amount.toPlainString();
    }

    public BigDecimal getValue() { return this.amount; }
    public Currency getCurrency() { return this.currency; }
}
