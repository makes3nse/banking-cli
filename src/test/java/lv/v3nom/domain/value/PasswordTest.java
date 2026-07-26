package lv.v3nom.domain.value;

import lv.v3nom.domain.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for Password")
class PasswordTest {

    @Mock
    private PasswordHasher hasherMock;

    @BeforeEach
    void setUp() {
        // Mock is already injected by extension
    }

    @Test
    @DisplayName("Should create hashed password from raw value")
    void fromRaw_validPassword_createsPassword() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValueHere");

        Password password = Password.fromRaw("securePass123", hasherMock);

        assertNotNull(password);
    }

    @Test
    @DisplayName("Should throw exception when raw password is null")
    void fromRaw_nullPassword_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Password.fromRaw(null, hasherMock)
        );
    }

    @Test
    @DisplayName("Should throw exception when raw password is blank")
    void fromRaw_blankPassword_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Password.fromRaw("", hasherMock)
        );
    }

    @Test
    @DisplayName("Should create password from existing hashed value")
    void of_existingHash_createsPassword() {
        String hash = "$2a$10$existingHashValue";

        Password password = Password.of(hash);

        assertEquals(hash, password.getValue());
    }

    @Test
    @DisplayName("Should throw exception when hash is null")
    void of_nullHash_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Password.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when hash is blank")
    void of_blankHash_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Password.of("")
        );
    }

    @Test
    @DisplayName("Should match raw password with correct hasher")
    void matches_correctPassword_returnsTrue() {
        when(hasherMock.matches(any(), any())).thenReturn(true);
        String hash = "$2a$10$someHash";

        Password password = Password.of(hash);

        assertTrue(password.matches("correctPass", hasherMock));
    }

    @Test
    @DisplayName("Should not match raw password with wrong value")
    void matches_wrongPassword_returnsFalse() {
        when(hasherMock.matches(any(), any())).thenReturn(false);
        String hash = "$2a$10$someHash";

        Password password = Password.of(hash);

        assertFalse(password.matches("wrongPass", hasherMock));
    }

    @Test
    @DisplayName("Should be equal to another password with same value")
    void equals_sameValue_returnsTrue() {
        String hash = "$2a$10$sameHash";

        Password p1 = Password.of(hash);
        Password p2 = Password.of(hash);

        assertTrue(p1.equals(p2));
    }

    @Test
    @DisplayName("Should not equal password with different value")
    void equals_differentValue_returnsFalse() {
        Password p1 = Password.of("$2a$10$hash1");
        Password p2 = Password.of("$2a$10$hash2");

        assertFalse(p1.equals(p2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        String hash = "$2a$10$hashCodeTest";

        Password p1 = Password.of(hash);
        Password p2 = Password.of(hash);

        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
