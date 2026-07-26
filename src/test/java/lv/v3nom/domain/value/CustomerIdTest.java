package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for CustomerId")
class CustomerIdTest {

    @Test
    @DisplayName("Should generate a valid customer ID")
    void generate_createsValidCustomerId() {
        CustomerId id = CustomerId.generate();

        assertNotNull(id);
        assertTrue(id.getValue().length() > 0);
    }

    @Test
    @DisplayName("Should create customer ID from existing string value")
    void of_existingValue_createsCustomerId() {
        CustomerId id = CustomerId.of("123456789012345");

        assertEquals("123456789012345", id.getValue());
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void of_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                CustomerId.of(null)
        );
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void of_blankValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                CustomerId.of("")
        );
    }

    @Test
    @DisplayName("Should be equal to another customer ID with same value")
    void equals_sameValue_returnsTrue() {
        CustomerId id1 = CustomerId.of("987654321098765");
        CustomerId id2 = CustomerId.of("987654321098765");

        assertTrue(id1.equals(id2));
    }

    @Test
    @DisplayName("Should not be equal to customer ID with different value")
    void equals_differentValue_returnsFalse() {
        CustomerId id1 = CustomerId.of("111111111111111");
        CustomerId id2 = CustomerId.of("222222222222222");

        assertFalse(id1.equals(id2));
    }

    @Test
    @DisplayName("Should have consistent hash code with equals")
    void hashCode_consistentWithEquals() {
        CustomerId id1 = CustomerId.of("444444444444444");
        CustomerId id2 = CustomerId.of("444444444444444");

        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
