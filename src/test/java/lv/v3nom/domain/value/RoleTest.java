package lv.v3nom.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for Role")
class RoleTest {

    @Test
    @DisplayName("Should provide CUSTOMER enum constant")
    void customer_constantExists() {
        assertNotNull(Role.CUSTOMER);
        assertEquals("CUSTOMER", Role.CUSTOMER.name());
    }

    @Test
    @DisplayName("Should provide TECHNICAL_ADMINISTRATOR enum constant")
    void technicalAdminConstantExists() {
        assertNotNull(Role.TECHNICAL_ADMINISTRATOR);
        assertEquals("TECHNICAL_ADMINISTRATOR", Role.TECHNICAL_ADMINISTRATOR.name());
    }

    @Test
    @DisplayName("Should provide SUPPORT_AGENT enum constant")
    void supportAgent_constantExists() {
        assertNotNull(Role.SUPPORT_AGENT);
        assertEquals("SUPPORT_AGENT", Role.SUPPORT_AGENT.name());
    }

    @Test
    @DisplayName("Should provide AUDITOR enum constant")
    void auditor_constantExists() {
        assertNotNull(Role.AUDITOR);
        assertEquals("AUDITOR", Role.AUDITOR.name());
    }

    @Test
    @DisplayName("Should return correct ordinal values")
    void ordinals_inCorrectOrder() {
        assertTrue(Role.CUSTOMER.ordinal() < Role.TECHNICAL_ADMINISTRATOR.ordinal());
        assertTrue(Role.SUPPORT_AGENT.ordinal() > Role.CUSTOMER.ordinal());
    }

    @Test
    @DisplayName("Should have all enum constants accessible via values")
    void values_returnsAllConstants() {
        int expectedCount = 4; // CUSTOMER, TECHNICAL_ADMINISTRATOR, SUPPORT_AGENT, AUDITOR
        assertEquals(expectedCount, Role.values().length);
    }

    @Test
    @DisplayName("Should return string name correctly")
    void toString_returnsName() {
        assertEquals("CUSTOMER", Role.CUSTOMER.name());
        assertEquals("TECHNICAL_ADMINISTRATOR", Role.TECHNICAL_ADMINISTRATOR.name());
        assertEquals("SUPPORT_AGENT", Role.SUPPORT_AGENT.name());
        assertEquals("AUDITOR", Role.AUDITOR.name());
    }

    @Test
    @DisplayName("Should be equal to same role")
    void equals_sameRole_returnsTrue() {
        assertTrue(Role.CUSTOMER.equals(Role.CUSTOMER));
    }

    @Test
    @DisplayName("Should not equal different roles")
    void equals_differentRoles_returnsFalse() {
        assertFalse(Role.CUSTOMER.equals(Role.AUDITOR));
    }
}
