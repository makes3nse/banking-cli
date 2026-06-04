package lv.v3nom.domain.value;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class MoneyTest {
    @Test
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = Money.of(new BigDecimal("50.00"),  Currency.EUR);
        assertEquals(0, money.getValue().compareTo(new BigDecimal("50.00")));
    }
    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
           Money.of(new BigDecimal("-10.00"), Currency.EUR);
        });
    }
    @Test
    void shouldAddTwoMoneyObjects() {
        Money money1 = Money.of(new BigDecimal("10.00"), Currency.EUR);
        Money money2 = Money.of(new BigDecimal("20.00"), Currency.EUR);
        Money result = money1.add(money2);
        assertEquals(0, result.getValue().compareTo(new BigDecimal("30.00")));
    }
}
