package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for Money")
class MoneyTest {

    @Test
    @DisplayName("Should create money with BigDecimal value")
    void of_bigDecimal_createsMoney() {
        Money money = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);

        assertEquals(0, BigDecimal.valueOf(100).setScale(2).compareTo(money.getValue()));
        assertEquals(Currency.USD, money.getCurrency());
    }

    @Test
    @DisplayName("Should create money with String value")
    void of_string_createsMoney() {
        Money money = Money.of("50.50", Currency.EUR);

        assertEquals(0, BigDecimal.valueOf(50.50).setScale(2).compareTo(money.getValue()));
        assertEquals(Currency.EUR, money.getCurrency());
    }

    @Test
    @DisplayName("Should create money with long value")
    void of_long_createsMoney() {
        Money money = Money.of(1000L, Currency.GBP);

        assertEquals(0, BigDecimal.valueOf(1000).setScale(2).compareTo(money.getValue()));
        assertEquals(Currency.GBP, money.getCurrency());
    }

    @Test
    @DisplayName("Should create zero money for a currency")
    void zero_createsZeroMoney() {
        Money money = Money.zero(Currency.USD);

        assertEquals(0, BigDecimal.ZERO.compareTo(money.getValue()));
        assertEquals(Currency.USD, money.getCurrency());
        assertTrue(money.isZero());
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void of_nullAmount_throwsException() {
        assertThrows(NullPointerException.class, () ->
                Money.of((BigDecimal) null, Currency.USD)
        );
    }

    @Test
    @DisplayName("Should throw exception when currency is null")
    void of_nullCurrency_throwsException() {
        assertThrows(NullPointerException.class, () ->
                Money.of(BigDecimal.valueOf(100).setScale(2), null)
        );
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void of_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Money.of(BigDecimal.valueOf(-50), Currency.USD)
        );
    }

    @Test
    @DisplayName("Should add two money amounts with same currency")
    void add_sameCurrency_successfullyAdds() {
        Money a = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money b = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.USD);
        Money result = a.add(b);

        assertEquals(0, BigDecimal.valueOf(150).setScale(2).compareTo(result.getValue()));
        assertEquals(Currency.USD, result.getCurrency());
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void add_differentCurrency_throwsException() {
        Money usd = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money eur = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.EUR);

        assertThrows(IllegalStateException.class, () ->
                usd.add(eur)
        );
    }

    @Test
    @DisplayName("Should subtract two money amounts with same currency")
    void subtract_sameCurrency_successfullySubtracts() {
        Money a = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);
        Money b = Money.of(BigDecimal.valueOf(30).setScale(2), Currency.EUR);
        Money result = a.subtract(b);

        assertEquals(0, BigDecimal.valueOf(70).setScale(2).compareTo(result.getValue()));
        assertEquals(Currency.EUR, result.getCurrency());
    }

    @Test
    @DisplayName("Should throw exception when subtracting different currencies")
    void subtract_differentCurrency_throwsException() {
        Money usd = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money gbp = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);

        assertThrows(IllegalStateException.class, () ->
                usd.subtract(gbp)
        );
    }

    @Test
    @DisplayName("Should multiply money by an integer")
    void multiply_successfullyMultiplies() {
        Money amount = Money.of(BigDecimal.valueOf(10).setScale(2), Currency.USD);
        Money result = amount.multiply(5);

        assertEquals(0, BigDecimal.valueOf(50).setScale(2).compareTo(result.getValue()));
        assertEquals(Currency.USD, result.getCurrency());
    }

    @Test
    @DisplayName("Should compare money with greater than")
    void isGreaterThan_largerAmount_returnsTrue() {
        Money a = Money.of(BigDecimal.valueOf(200).setScale(2), Currency.EUR);
        Money b = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);

        assertTrue(a.isGreaterThan(b));
    }

    @Test
    @DisplayName("Should compare money with less than")
    void isLessThan_smallerAmount_returnsTrue() {
        Money a = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);
        Money b = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.GBP);

        assertTrue(a.isLessThan(b));
    }

    @Test
    @DisplayName("Should compare money with greater than or equal")
    void isGreaterThanOrEqualTo_sameAmount_returnsTrue() {
        Money a = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money b = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);

        assertTrue(a.isGreaterThanOrEqualTo(b));
    }

    @Test
    @DisplayName("Should detect zero money")
    void isZero_zeroMoney_returnsTrue() {
        Money zero = Money.zero(Currency.EUR);

        assertTrue(zero.isZero());
    }

    @Test
    @DisplayName("Should detect positive money")
    void isPositive_positiveAmount_returnsTrue() {
        Money amount = Money.of(BigDecimal.valueOf(10).setScale(2), Currency.GBP);

        assertTrue(amount.isPositive());
    }

    @Test
    @DisplayName("Should detect negative or zero (should not happen for constructor, but tests edge)")
    void isNegativeOrZero_zeroReturnsTrue() {
        Money zero = Money.zero(Currency.USD);

        assertTrue(zero.isNegativeOrZero());
    }

    @Test
    @DisplayName("Should compare two money amounts")
    void compareTo_largerAmount_returnsPositive() {
        Money a = Money.of(BigDecimal.valueOf(200).setScale(2), Currency.EUR);
        Money b = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);

        assertTrue(a.compareTo(b) > 0);
    }

    @Test
    @DisplayName("Should be equal to another money with same amount and currency")
    void equals_sameAmountAndCurrency_returnsTrue() {
        Money a = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money b = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);

        assertTrue(a.equals(b));
    }

    @Test
    @DisplayName("Should not be equal to money with different amount")
    void equals_differentAmount_returnsFalse() {
        Money a = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);
        Money b = Money.of(BigDecimal.valueOf(200).setScale(2), Currency.EUR);

        assertFalse(a.equals(b));
    }

    @Test
    @DisplayName("Should not be equal to money with different currency")
    void equals_differentCurrency_returnsFalse() {
        Money usd = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);
        Money eur = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);

        assertFalse(usd.equals(eur));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        Money a = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);
        Money b = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Should return string representation of amount")
    void toString_returnsAmount() {
        Money money = Money.of(BigDecimal.valueOf(123.45).setScale(2), Currency.EUR);

        assertEquals("123.45", money.toString());
    }

    @Test
    @DisplayName("Should round to two decimal places for standard currencies")
    void scale_scalesToTwoDecimals() {
        Money money = Money.of("10.123", Currency.EUR);

        assertEquals("10.12", money.toString());
        assertEquals(0, new BigDecimal("10.12").compareTo(money.getValue()));
    }
}
