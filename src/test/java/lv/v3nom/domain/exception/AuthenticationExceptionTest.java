package lv.v3nom.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AuthenticationException")
class AuthenticationExceptionTest {

    @Test
    @DisplayName("Default constructor should produce correct message")
    void defaultConstructor_messageIsCorrect() {
        AuthenticationException ex = new AuthenticationException();

        assertEquals("Authentication failed", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor with AccountId should set accountId correctly")
    void constructorWithAccountId_setsFields() {
        lv.v3nom.domain.value.AccountId accountId = lv.v3nom.domain.value.AccountId.of("123456789012345");

        AuthenticationException ex = new AuthenticationException(accountId);

        assertEquals(accountId, ex.getAccountId());
        assertNull(ex.getCustomerId());
        assertNull(ex.getToken());
    }

    @Test
    @DisplayName("Constructor with CustomerId should set customerId correctly")
    void constructorWithCustomerId_setsFields() {
        lv.v3nom.domain.value.CustomerId customerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");

        AuthenticationException ex = new AuthenticationException(customerId);

        assertNull(ex.getAccountId());
        assertEquals(customerId, ex.getCustomerId());
        assertNull(ex.getToken());
    }

    @Test
    @DisplayName("Constructor with Token should set token correctly")
    void constructorWithToken_setsFields() {
        lv.v3nom.domain.value.CustomerId customerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        lv.v3nom.domain.value.Token token = lv.v3nom.domain.value.Token.of("tokenValue", expiry, customerId);

        AuthenticationException ex = new AuthenticationException(token);

        assertNull(ex.getAccountId());
        assertNull(ex.getCustomerId());
        assertEquals(token, ex.getToken());
    }
}
