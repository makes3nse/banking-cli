package lv.v3nom.domain.model;

import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for Customer")
class CustomerTest {

    private LocalDateTime now;
    @Mock
    private PasswordHasher hasherMock;

    @BeforeEach
    void setUp() {
        // Do nothing extra - mock is injected by extension
    }

    @Test
    @DisplayName("Should register new customer with valid credentials")
    void register_validCredentials_createsCustomer() {
        String name = "John Doe";
        String email = "john@example.com";
        String phone = "+1234567890";
        String password = "securePass123";

        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");

        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 0, 0);
        Customer customer = Customer.register(name, email, phone, password, hasherMock, createdAt);

        assertNotNull(customer);
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail().getValue());
        assertEquals("+1234567890", customer.getPhoneNumber().getValue());
        assertEquals(CustomerStatus.ACTIVE, customer.getCustomerStatus());
        assertEquals(Role.CUSTOMER, customer.getRole());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(createdAt, customer.getUpdatedAt());
    }

    @Test
    @DisplayName("Should register with trimmed name")
    void register_withSpaces_trimsName() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");

        LocalDateTime createdAt = LocalDateTime.of(2025, 3, 15, 12, 0);
        Customer customer = Customer.register("   Jane Smith   ", "jane@test.com", "+44987654321", "pass123", hasherMock, createdAt);

        assertEquals("Jane Smith", customer.getName());
    }

    @Test
    @DisplayName("Should throw exception when name is too short")
    void register_nameTooShort_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Customer.register("J", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Should throw exception when name exceeds max length")
    void register_nameTooLong_throwsException() {
        String longName = "A".repeat(60);

        assertThrows(IllegalArgumentException.class, () ->
                Customer.register(longName, "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void register_blankName_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Customer.register("", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void register_nullName_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Customer.register(null, "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Should reconstitute customer from persisted state")
    void reconstitute_restoresCustomerState() {
        CustomerId id = CustomerId.of("555555555555555");
        Role role = Role.AUDITOR;
        CustomerStatus status = CustomerStatus.ACTIVE;
        String name = "Reconstituted User";
        EmailAddress email = EmailAddress.of("recon@test.com");
        PhoneNumber phone = PhoneNumber.of("+19998887777");
        Password password = Password.of("$2a$10$existingHash");

        LocalDateTime created = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 12, 31, 23, 59);

        Customer customer = Customer.reconstitute(id, role, status, name, email, phone, password, hasherMock, created, updated);

        assertEquals(id, customer.getId());
        assertEquals(role, customer.getRole());
        assertEquals(status, customer.getCustomerStatus());
        assertEquals(name, customer.getName());
        assertEquals(email.getValue(), customer.getEmail().getValue());
        assertEquals(phone.getValue(), customer.getPhoneNumber().getValue());
        assertEquals(created, customer.getCreatedAt());
        assertEquals(updated, customer.getUpdatedAt());
    }

    @Test
    @DisplayName("Should change name to valid new name")
    void changeName_validNewName_success() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Old Name", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        customer.changeName("New Valid Name");

        assertEquals("New Valid Name", customer.getName());
    }

    @Test
    @DisplayName("Should throw exception when changing name on non-active status")
    void changeName_nonActiveStatus_throwsException() {
        CustomerId id = CustomerId.of("666666666666666");
        Password password = Password.of("$2a$10$someHash");

        // Create customer via reconstitute with SUSPENDED status
        Customer suspendedCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.SUSPENDED, "Suspended User",
                EmailAddress.of("suspended@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(IllegalStateException.class, () ->
                suspendedCustomer.changeName("New Name")
        );
    }

    @Test
    @DisplayName("Should throw exception when new name is blank")
    void changeName_blankName_throwsException() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Existing Name", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class, () ->
                customer.changeName("")
        );
    }

    @Test
    @DisplayName("Should throw exception when new name is null")
    void changeName_nullName_throwsException() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Existing Name", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class, () ->
                customer.changeName(null)
        );
    }

    @Test
    @DisplayName("Should change email to new valid address")
    void changeEmail_validNewAddress_success() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Test User", "old@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        customer.changeEmail(EmailAddress.of("new@test.com"));

        assertEquals("new@test.com", customer.getEmail().getValue());
    }

    @Test
    @DisplayName("Should throw exception when changing to same email")
    void changeEmail_sameAddress_throwsException() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Test User", "same@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class, () ->
                customer.changeEmail(EmailAddress.of("same@test.com"))
        );
    }

    @Test
    @DisplayName("Should throw exception when changing email on non-active status")
    void changeEmail_nonActiveStatus_throwsException() {
        CustomerId id = CustomerId.of("777777777777777");
        Password password = Password.of("$2a$10$someHash");

        Customer closedCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.CLOSED, "Closed User",
                EmailAddress.of("closed@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(IllegalStateException.class, () ->
                closedCustomer.changeEmail(EmailAddress.of("new@test.com"))
        );
    }

    @Test
    @DisplayName("Should change phone number to new valid number")
    void changePhoneNumber_validNewNumber_success() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Test User", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        customer.changePhoneNumber(PhoneNumber.of("+987654321"));

        assertEquals("+987654321", customer.getPhoneNumber().getValue());
    }

    @Test
    @DisplayName("Should throw exception when changing to same phone number")
    void changePhoneNumber_sameNumber_throwsException() {
        when(hasherMock.hash(any())).thenReturn("$2a$10$hashedValue");
        Customer customer = Customer.register("Test User", "test@test.com", "+1234567890", "pass123", hasherMock, LocalDateTime.of(2025, 1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class, () ->
                customer.changePhoneNumber(PhoneNumber.of("+1234567890"))
        );
    }

    @Test
    @DisplayName("Should throw exception when changing phone on non-active status")
    void changePhoneNumber_nonActiveStatus_throwsException() {
        CustomerId id = CustomerId.of("888888888888888");
        Password password = Password.of("$2a$10$someHash");

        Customer bannedCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.BANNED, "Banned User",
                EmailAddress.of("banned@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(IllegalStateException.class, () ->
                bannedCustomer.changePhoneNumber(PhoneNumber.of("+987654321"))
        );
    }

    @Test
    @DisplayName("Should change password when old is correct and new differs")
    void changePassword_validChange_success() {
        // Mock the hash behavior
        when(hasherMock.hash(any())).thenReturn("$2a$10$newHash");
        // Mock matches to return true ONLY for the old password
        when(hasherMock.matches(eq("oldPass"), anyString())).thenReturn(true);
        // Mock matches to return false for the new password
        when(hasherMock.matches(eq("newSecurePass"), anyString())).thenReturn(false);

        String currentHash = "$2a$10$oldHash";
        CustomerId id = CustomerId.of("999999999999999");

        // Create customer with existing password hash via reconstitute
        Password existingPassword = Password.of(currentHash);
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 0, 0);
        Customer customer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.ACTIVE, "Test User",
                EmailAddress.of("test@test.com"), PhoneNumber.of("+1234567890"),
                existingPassword, hasherMock, createdAt, createdAt);

        // Change password - verify password is updated
        customer.changePassword("oldPass", "newSecurePass", hasherMock);

        // Verify that the password value changed (matches new hash)
        assertFalse(existingPassword.equals(customer.getPassword()));
    }

    @Test
    @DisplayName("Should throw exception when old password is incorrect")
    void changePassword_wrongOldPassword_throwsException() {
        Password existingPassword = Password.of("$2a$10$currentHash");
        CustomerId id = CustomerId.of("aaaaaabbbbbbbb");
        LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 0, 0);

        Customer customer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.ACTIVE, "Test User",
                EmailAddress.of("test@test.com"), PhoneNumber.of("+1234567890"),
                existingPassword, hasherMock, createdAt, createdAt);

        assertThrows(IllegalStateException.class, () ->
                customer.changePassword("wrongOld", "newPass", hasherMock)
        );
    }

    @Test
    @DisplayName("Should throw exception when new password is same as old")
    void changePassword_sameAsOld_throwsException() {
        Password existingPassword = Password.of("$2a$10$sameHash");
        CustomerId id = CustomerId.of("ccccccdddddddd");
        LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 0, 0);

        Customer customer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.ACTIVE, "Test User",
                EmailAddress.of("test@test.com"), PhoneNumber.of("+1234567890"),
                existingPassword, hasherMock, createdAt, createdAt);

        assertThrows(IllegalStateException.class, () ->
                customer.changePassword("sameAsOld", "sameAsOld", hasherMock)
        );
    }

    @Test
    @DisplayName("Should return true when can open account and status is ACTIVE")
    void canOpenAccount_activeStatus_returnsTrue() {
        CustomerId id = CustomerId.of("eeeeefffffff");
        Password password = Password.of("$2a$10$someHash");

        Customer activeCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.ACTIVE, "Active User",
                EmailAddress.of("active@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertTrue(activeCustomer.canOpenAccount());
    }

    @Test
    @DisplayName("Should return false when can open account and status is SUSPENDED")
    void canOpenAccount_suspendedStatus_returnsFalse() {
        CustomerId id = CustomerId.of("gggghhhhhh");
        Password password = Password.of("$2a$10$someHash");

        Customer suspendedCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.SUSPENDED, "Suspended User",
                EmailAddress.of("suspended@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertFalse(suspendedCustomer.canOpenAccount());
    }

    @Test
    @DisplayName("Should return false when can open account and status is CLOSED")
    void canOpenAccount_closedStatus_returnsFalse() {
        CustomerId id = CustomerId.of("jjjjjkkkkk");
        Password password = Password.of("$2a$10$someHash");

        Customer closedCustomer = Customer.reconstitute(
                id, Role.CUSTOMER, CustomerStatus.CLOSED, "Closed User",
                EmailAddress.of("closed@test.com"), PhoneNumber.of("+1234567890"),
                password, hasherMock, LocalDateTime.now(), LocalDateTime.now());

        assertFalse(closedCustomer.canOpenAccount());
    }
}
