package lv.v3nom.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AccountNotFoundException")
class AccountNotFoundExceptionTest {

    @Test
    @DisplayName("Default constructor should produce correct message")
    void defaultConstructor_messageIsCorrect() {
        AccountNotFoundException ex = new AccountNotFoundException();

        assertEquals("Account not found", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor with AccountId should set accountId correctly")
    void constructorWithAccountId_setsFields() {
        lv.v3nom.domain.value.AccountId accountId = lv.v3nom.domain.value.AccountId.of("123456789012345");

        AccountNotFoundException ex = new AccountNotFoundException(accountId);

        assertEquals(accountId, ex.getAccountId());
        assertNull(ex.getOwnerId());
        assertNull(ex.getAccountStatus());
    }

    @Test
    @DisplayName("Constructor with CustomerId should set ownerId correctly")
    void constructorWithCustomerId_setsFields() {
        lv.v3nom.domain.value.CustomerId customerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");

        AccountNotFoundException ex = new AccountNotFoundException(customerId);

        assertNull(ex.getAccountId());
        assertEquals(customerId, ex.getOwnerId());
        assertNull(ex.getAccountStatus());
    }

    @Test
    @DisplayName("Constructor with AccountStatus should set accountStatus correctly")
    void constructorWithAccountStatus_setsFields() {
        lv.v3nom.domain.value.AccountStatus status = lv.v3nom.domain.value.AccountStatus.CLOSED;

        AccountNotFoundException ex = new AccountNotFoundException(status);

        assertNull(ex.getAccountId());
        assertNull(ex.getOwnerId());
        assertEquals(status, ex.getAccountStatus());
    }
}
