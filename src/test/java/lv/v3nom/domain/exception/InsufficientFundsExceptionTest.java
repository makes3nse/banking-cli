package lv.v3nom.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for InsufficientFundsException")
class InsufficientFundsExceptionTest {

    @Test
    @DisplayName("Default constructor should produce correct message")
    void defaultConstructor_messageIsCorrect() {
        InsufficientFundsException ex = new InsufficientFundsException();

        assertEquals("Operation prohibited. Insufficient funds", ex.getMessage());
        assertNull(ex.getBalance());
        assertNull(ex.getAttemptedAmount());
        assertNull(ex.getTransactionType());
    }

    @Test
    @DisplayName("Constructor with TransactionType should set transactionType correctly")
    void constructorWithTransactionType_setsFields() {
        lv.v3nom.domain.value.TransactionType type = lv.v3nom.domain.value.TransactionType.DEPOSIT;

        InsufficientFundsException ex = new InsufficientFundsException(type);

        assertEquals("DEPOSIT not possible. Insufficient funds", ex.getMessage());
        assertNull(ex.getBalance());
        assertNull(ex.getAttemptedAmount());
        assertEquals(type, ex.getTransactionType());
    }

    @Test
    @DisplayName("Constructor with Money balance and amount should set both correctly")
    void constructorWithMoney_setsFields() {
        lv.v3nom.domain.value.Money balance = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(10).setScale(2), lv.v3nom.domain.value.Currency.EUR);
        lv.v3nom.domain.value.Money attemptedAmount = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(100).setScale(2), lv.v3nom.domain.value.Currency.EUR);

        InsufficientFundsException ex = new InsufficientFundsException(balance, attemptedAmount);

        assertEquals("Value 100.00 is too large. Balance: ", ex.getMessage());
        assertEquals(balance, ex.getBalance());
        assertEquals(attemptedAmount, ex.getAttemptedAmount());
        assertNull(ex.getTransactionType());
    }
}
