package lv.v3nom.domain.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money>{
    private final BigDecimal amount;

    public static final Money ZERO =
            new Money(BigDecimal.ZERO);

    private Money(BigDecimal amount) {
        Objects.requireNonNull(amount, "Money amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative" + amount);
        } else {
            this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }
    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    /*
    wrong shit

    public Money add(BigDecimal amountToAdd) {
        BigDecimal newAmount = this.amount.add(amountToAdd);
        return new Money(newAmount);
    }*/

    // MATH
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    // COMPARSION
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
    public BigDecimal getAmount() {
        return this.amount;
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
        Money money = (Money) o;
        return this.amount.compareTo(money.amount) == 0;
    }
    @Override
    public int hashCode() {
        return this.amount.hashCode();
    }
    @Override
    public String toString() {
        return this.amount.toPlainString();
    }
}
