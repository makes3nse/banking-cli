package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AccountId")
class AccountIdTest {

    @Test
    @DisplayName("Should generate a valid account ID")
    void generate_createsValidAccountId() {
        AccountId id = AccountId.generate();

        assertNotNull(id);
        assertTrue(id.getValue().length() > 0);
    }

    @Test
    @DisplayName("Should create account ID from existing string value")
    void of_existingValue_createsAccountId() {
        AccountId id = AccountId.of("123456789012345");

        assertEquals("123456789012345", id.getValue());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountId.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void of_blankValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountId.of("")
        );
    }

    @Test
    @DisplayName("Should throw exception when value contains only spaces")
    void of_spacesOnly_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                AccountId.of("   ")
        );
    }

    @Test
    @DisplayName("Should be equal to another account ID with same value")
    void equals_sameValue_returnsTrue() {
        AccountId id1 = AccountId.of("987654321098765");
        AccountId id2 = AccountId.of("987654321098765");

        assertTrue(id1.equals(id2));
    }

    @Test
    @DisplayName("Should not be equal to account ID with different value")
    void equals_differentValue_returnsFalse() {
        AccountId id1 = AccountId.of("111111111111111");
        AccountId id2 = AccountId.of("222222222222222");

        assertFalse(id1.equals(id2));
    }

    @Test
    @DisplayName("Should not be equal to null")
    void equals_null_returnsFalse() {
        AccountId id = AccountId.of("333333333333333");

        assertFalse(id.equals(null));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        AccountId id1 = AccountId.of("444444444444444");
        AccountId id2 = AccountId.of("444444444444444");

        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should have different hash codes for different values")
    void hashCode_differentValues() {
        AccountId id1 = AccountId.of("555555555555555");
        AccountId id2 = AccountId.of("666666666666666");

        assertNotEquals(id1.hashCode(), id2.hashCode());
    }
}
