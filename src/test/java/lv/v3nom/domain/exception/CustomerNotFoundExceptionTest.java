package lv.v3nom.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for CustomerNotFoundException")
class CustomerNotFoundExceptionTest {

    @Test
    @DisplayName("Default constructor should produce correct message")
    void defaultConstructor_messageIsCorrect() {
        CustomerNotFoundException ex = new CustomerNotFoundException();

        assertEquals("Customers not found", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor with CustomerId should set customerId correctly")
    void constructorWithCustomerId_setsFields() {
        lv.v3nom.domain.value.CustomerId id = lv.v3nom.domain.value.CustomerId.of("123456789012345");

        CustomerNotFoundException ex = new CustomerNotFoundException(id);

        assertEquals(id, ex.getCustomerId());
        assertNull(ex.getEmailAddress());
        assertNull(ex.getPhoneNumber());
        assertNull(ex.getCustomerStatus());
    }

    @Test
    @DisplayName("Constructor with Email should set emailAddress correctly")
    void constructorWithEmail_setsFields() {
        lv.v3nom.domain.value.EmailAddress email = lv.v3nom.domain.value.EmailAddress.of("test@test.com");

        CustomerNotFoundException ex = new CustomerNotFoundException(email);

        assertNull(ex.getCustomerId());
        assertEquals(email, ex.getEmailAddress());
        assertNull(ex.getPhoneNumber());
        assertNull(ex.getCustomerStatus());
    }

    @Test
    @DisplayName("Constructor with Phone should set phoneNumber correctly")
    void constructorWithPhone_setsFields() {
        lv.v3nom.domain.value.PhoneNumber phone = lv.v3nom.domain.value.PhoneNumber.of("+1234567890");

        CustomerNotFoundException ex = new CustomerNotFoundException(phone);

        assertNull(ex.getCustomerId());
        assertNull(ex.getEmailAddress());
        assertEquals(phone, ex.getPhoneNumber());
        assertNull(ex.getCustomerStatus());
    }

    @Test
    @DisplayName("Constructor with CustomerStatus should set customerStatus correctly")
    void constructorWithStatus_setsFields() {
        lv.v3nom.domain.value.CustomerStatus status = lv.v3nom.domain.value.CustomerStatus.CLOSED;

        CustomerNotFoundException ex = new CustomerNotFoundException(status);

        assertNull(ex.getCustomerId());
        assertNull(ex.getEmailAddress());
        assertNull(ex.getPhoneNumber());
        assertEquals(status, ex.getCustomerStatus());
    }
}
