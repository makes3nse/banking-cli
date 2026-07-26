package lv.v3nom.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for CustomerMapper")
@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    @Mock
    private lv.v3nom.domain.security.PasswordHasher hasherMock;

    private lv.v3nom.domain.model.Customer customer;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 1, 1, 0, 0);
        // Reconstitute a customer with known state for testing mappers
        lv.v3nom.domain.value.CustomerId id = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        lv.v3nom.domain.value.Role role = lv.v3nom.domain.value.Role.CUSTOMER;
        lv.v3nom.domain.value.CustomerStatus status = lv.v3nom.domain.value.CustomerStatus.ACTIVE;
        String name = "Test User";
        lv.v3nom.domain.value.EmailAddress email = lv.v3nom.domain.value.EmailAddress.of("test@test.com");
        lv.v3nom.domain.value.PhoneNumber phone = lv.v3nom.domain.value.PhoneNumber.of("+1234567890");
        lv.v3nom.domain.value.Password password = lv.v3nom.domain.value.Password.of("$2a$10$hashedValue");

        customer = lv.v3nom.domain.model.Customer.reconstitute(
                id, role, status, name, email, phone, password, hasherMock, now, now);
    }

    @Test
    @DisplayName("Should convert Customer to response with success status")
    void toResponse_customerWithActiveStatus_returnsCustomerResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("SUCCESS");

        var response = CustomerMapper.toResponse(customer, opStatus);

        assertEquals("123456789012345", response.getCustomerId());
        assertEquals("Test User", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("+1234567890", response.getPhone());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("SUCCESS", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should convert Customer to register response with session token")
    void toRegisterResponse_returnsRegisterCustomerResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("SUCCESS");
        String sessionToken = "abc123session456";

        var response = CustomerMapper.toRegisterResponse(customer, sessionToken, opStatus);

        assertEquals("123456789012345", response.getCustomerId());
        assertEquals("Test User", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("+1234567890", response.getPhone());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("SUCCESS", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should create failure response for customer")
    void failureResponse_returnsFailureCustomerResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = CustomerMapper.failureResponse("123456789012345", opStatus);

        assertEquals("123456789012345", response.getCustomerId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getStatus());
        assertEquals("FAILURE", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should create failure register response")
    void failureRegisterResponse_returnsFailureRegisterResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = CustomerMapper.failureRegisterResponse("123456789012345", opStatus);

        assertEquals("123456789012345", response.getCustomerId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getStatus());
        assertEquals("FAILURE", response.getOperationStatus());
    }
}
