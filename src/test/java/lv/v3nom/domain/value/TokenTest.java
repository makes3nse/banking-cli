package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for Token")
class TokenTest {

    @Test
    @DisplayName("Should create a valid token with long value and future expiry")
    void create_validToken_createsToken() {
        String tokenValue = "a".repeat(40) + "b".repeat(32); // 72 chars > 32
        CustomerId customerId = CustomerId.of("111111111111111");
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime expiry = now.plusHours(24);

        Token token = Token.create(tokenValue, expiry, customerId, now);

        assertEquals(tokenValue.substring(0, 42) + "...", token.toString().substring(0, 42) + "...");
    }

    @Test
    @DisplayName("Should create token from existing state")
    void of_existingState_createsToken() {
        String value = "existingTokenValue32charsLong";
        CustomerId customerId = CustomerId.of("555555555555555");
        LocalDateTime expiry = LocalDateTime.of(2025, 12, 31, 23, 59);

        Token token = Token.of(value, expiry, customerId);

        assertEquals(value, token.getValue());
        assertEquals(expiry, token.getExpiry());
        assertEquals(customerId, token.getCustomerId());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void create_nullValue_throwsException() {
        CustomerId customerId = CustomerId.of("123456789012345");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                Token.create(null, expiry, customerId, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void create_blankValue_throwsException() {
        CustomerId customerId = CustomerId.of("123456789012345");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                Token.create("", expiry, customerId, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is too short")
    void create_tooShortValue_throwsException() {
        CustomerId customerId = CustomerId.of("123456789012345");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                Token.create("short", expiry, customerId, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when expiry is null")
    void create_nullExpiry_throwsException() {
        CustomerId customerId = CustomerId.of("123456789012345");
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () ->
                Token.create("a".repeat(32), null, customerId, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when customer ID is null")
    void create_nullCustomerId_throwsException() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                Token.create("a".repeat(32), expiry, null, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when expiry is in the past")
    void create_pastExpiry_throwsException() {
        CustomerId customerId = CustomerId.of("123456789012345");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.minusHours(1);

        assertThrows(IllegalArgumentException.class, () ->
                Token.create("a".repeat(32), expiry, customerId, now)
        );
    }

    @Test
    @DisplayName("Should be valid when current time is before expiry")
    void isValid_futureExpiry_returnsTrue() {
        CustomerId customerId = CustomerId.of("111111111111111");
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        LocalDateTime expiry = now.plusHours(24);

        Token token = Token.create("a".repeat(32), expiry, customerId, now);

        assertTrue(token.isValid(now));
    }

    @Test
    @DisplayName("Should be invalid when current time is after expiry")
    void isValid_pastExpiry_returnsFalse() {
        CustomerId customerId = CustomerId.of("111111111111111");
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 12, 0);
        LocalDateTime expiry = now.minusHours(1);

        Token token = Token.of("a".repeat(32), expiry, customerId);

        assertFalse(token.isValid(now));
    }

    @Test
    @DisplayName("Should be equal to another token with same values")
    void equals_sameValues_returnsTrue() {
        String value = "tokenValue123456789012";
        CustomerId customerId = CustomerId.of("999999999999999");
        LocalDateTime expiry = LocalDateTime.of(2025, 12, 31, 23, 59);

        Token t1 = Token.of(value, expiry, customerId);
        Token t2 = Token.of(value, expiry, customerId);

        assertTrue(t1.equals(t2));
    }

    @Test
    @DisplayName("Should not equal token with different value")
    void equals_differentValue_returnsFalse() {
        CustomerId customerId = CustomerId.of("999999999999999");
        LocalDateTime expiry = LocalDateTime.of(2025, 12, 31, 23, 59);

        Token t1 = Token.of("tokenValue12345678901", expiry, customerId);
        Token t2 = Token.of("differentToken1234567890", expiry, customerId);

        assertFalse(t1.equals(t2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        String value = "hashTestValue123456";
        CustomerId customerId = CustomerId.of("777777777777777");
        LocalDateTime expiry = LocalDateTime.of(2025, 6, 15, 12, 0);

        Token t1 = Token.of(value, expiry, customerId);
        Token t2 = Token.of(value, expiry, customerId);

        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    @DisplayName("Should return correct value getter")
    void getValue_returnsCorrectValue() {
        String value = "getterTestValue32";
        CustomerId customerId = CustomerId.of("888888888888888");
        LocalDateTime expiry = LocalDateTime.of(2025, 6, 15, 12, 0);

        Token token = Token.of(value, expiry, customerId);

        assertEquals(value, token.getValue());
    }
}
