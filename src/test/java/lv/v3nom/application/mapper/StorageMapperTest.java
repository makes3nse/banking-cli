package lv.v3nom.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for StorageMapper")
@ExtendWith(MockitoExtension.class)
class StorageMapperTest {

    @Mock
    private lv.v3nom.domain.security.PasswordHasher hasherMock;

    private lv.v3nom.domain.model.Account account;
    private lv.v3nom.domain.model.Customer customer;
    private lv.v3nom.domain.model.Transaction transaction;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 1, 1, 0, 0);

        // Setup test account
        lv.v3nom.domain.value.AccountId accountId = lv.v3nom.domain.value.AccountId.of("987654321098765");
        lv.v3nom.domain.value.CustomerId ownerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        lv.v3nom.domain.value.Currency currency = lv.v3nom.domain.value.Currency.EUR;
        lv.v3nom.domain.value.Money balance = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(500).setScale(2), currency);
        lv.v3nom.domain.value.AccountStatus status = lv.v3nom.domain.value.AccountStatus.ACTIVE;

        account = lv.v3nom.domain.model.Account.reconstitute(
                accountId, ownerId, currency, balance, status, now, now);

        // Setup test customer
        lv.v3nom.domain.value.CustomerId customerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        lv.v3nom.domain.value.Role role = lv.v3nom.domain.value.Role.CUSTOMER;
        lv.v3nom.domain.value.CustomerStatus customerStatus = lv.v3nom.domain.value.CustomerStatus.ACTIVE;
        String name = "Test User";
        lv.v3nom.domain.value.EmailAddress email = lv.v3nom.domain.value.EmailAddress.of("test@test.com");
        lv.v3nom.domain.value.PhoneNumber phone = lv.v3nom.domain.value.PhoneNumber.of("+1234567890");
        lv.v3nom.domain.value.Password password = lv.v3nom.domain.value.Password.of("$2a$10$hashedValue");

        customer = lv.v3nom.domain.model.Customer.reconstitute(
                customerId, role, customerStatus, name, email, phone, password, hasherMock, now, now);

        // Setup test transaction (completed)
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime completedAt = LocalDateTime.of(2025, 1, 2, 8, 0);
        lv.v3nom.domain.value.TransactionId transactionId = lv.v3nom.domain.value.TransactionId.generate();
        lv.v3nom.domain.value.Currency txnCurrency = lv.v3nom.domain.value.Currency.EUR;
        lv.v3nom.domain.value.Money amount = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(250).setScale(2), txnCurrency);
        lv.v3nom.domain.value.AccountId sourceAccount = lv.v3nom.domain.value.AccountId.of("111111111111111");
        lv.v3nom.domain.value.AccountId targetAccount = lv.v3nom.domain.value.AccountId.of("222222222222222");
        lv.v3nom.domain.value.TransactionType type = lv.v3nom.domain.value.TransactionType.TRANSFER;

        transaction = lv.v3nom.domain.model.Transaction.reconstitute(
                transactionId, txnCurrency, amount, sourceAccount, targetAccount, type,
                lv.v3nom.domain.value.TransactionStatus.COMPLETED, createdAt, completedAt, null, null);
    }

    @Test
    @DisplayName("Should convert Account to storage DTO")
    void toStorageAccount_convertsCorrectly() {
        var dto = StorageMapper.toStorageAccount(account);

        assertEquals("987654321098765", dto.getAccountId());
        assertEquals("123456789012345", dto.getOwnerId());
        assertEquals("EUR", dto.getCurrency());
        assertEquals("500.00", dto.getBalance()); // Money.toString() uses toPlainString
        assertEquals("ACTIVE", dto.getAccountStatus());
    }

    @Test
    @DisplayName("Should convert Account storage DTO back to domain model")
    void fromStorageAccount_restoresCorrectly() {
        lv.v3nom.domain.value.AccountId accountId = lv.v3nom.domain.value.AccountId.of("987654321098765");
        lv.v3nom.domain.value.CustomerId ownerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");

        var dto = new lv.v3nom.application.dto.storage.AccountStorageDTO(
                accountId.getValue(),
                ownerId.getValue(),
                "EUR",
                "500.00",
                "ACTIVE",
                "2025-01-01T00:00:00",
                "2025-01-01T00:00:00"
        );

        lv.v3nom.domain.model.Account restored = StorageMapper.fromStorageAccount(dto);

        assertEquals(accountId, restored.getAccountId());
        assertEquals(ownerId, restored.getOwnerId());
        assertEquals(lv.v3nom.domain.value.Currency.EUR, restored.getCurrency());
        assertEquals(lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(500).setScale(2), lv.v3nom.domain.value.Currency.EUR), restored.getBalance());
        assertEquals(lv.v3nom.domain.value.AccountStatus.ACTIVE, restored.getAccountStatus());
    }

    @Test
    @DisplayName("Should convert Customer to storage DTO")
    void toStorageCustomer_convertsCorrectly() {
        var dto = StorageMapper.toStorageCustomer(customer);

        assertEquals("123456789012345", dto.getId());
        assertEquals("CUSTOMER", dto.getRole());
        assertEquals("ACTIVE", dto.getCustomerStatus());
        assertEquals("Test User", dto.getName());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("+1234567890", dto.getPhoneNumber());
    }

    @Test
    @DisplayName("Should convert Customer storage DTO back to domain model")
    void fromStorageCustomer_restoresCorrectly() {
        var dto = new lv.v3nom.application.dto.storage.CustomerStorageDTO(
                "123456789012345",
                "CUSTOMER",
                "ACTIVE",
                "Test User",
                "test@test.com",
                "+1234567890",
                "$2a$10$hashedValue",
                "2025-01-01T00:00:00",
                "2025-01-01T00:00:00"
        );

        lv.v3nom.domain.model.Customer restored = StorageMapper.fromStorageCustomer(dto, hasherMock);

        assertEquals(lv.v3nom.domain.value.CustomerId.of("123456789012345"), restored.getId());
        assertEquals(lv.v3nom.domain.value.Role.CUSTOMER, restored.getRole());
        assertEquals(lv.v3nom.domain.value.CustomerStatus.ACTIVE, restored.getCustomerStatus());
        assertEquals("Test User", restored.getName());
    }

    @Test
    @DisplayName("Should convert Transaction to storage DTO")
    void toStorageTransaction_convertsCorrectly() {
        var dto = StorageMapper.toStorageTransaction(transaction);

        assertNotNull(dto.getTransactionId());
        assertEquals("EUR", dto.getCurrency());
        assertEquals(lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(250).setScale(2), lv.v3nom.domain.value.Currency.EUR).getValue().toString(), dto.getAmount());
    }

    @Test
    @DisplayName("Should convert Transaction storage DTO back to domain model")
    void fromStorageTransaction_restoresCorrectly() {
        var dto = new lv.v3nom.application.dto.storage.TransactionStorageDTO(
                "abc123transactionId",
                "EUR",
                "250.00",
                "111111111111111",
                "222222222222222",
                "TRANSFER",
                "COMPLETED",
                "2025-01-01T12:00:00",
                "2025-01-02T08:00:00",
                null,
                null
        );

        lv.v3nom.domain.model.Transaction restored = StorageMapper.fromStorageTransaction(dto);

        assertEquals("COMPLETED", restored.getTransactionStatus().getValue());
        assertEquals(lv.v3nom.domain.value.Currency.EUR, restored.getCurrency());
        assertEquals(BigDecimal.valueOf(250).setScale(2), restored.getAmount().getValue());
    }

    @Test
    @DisplayName("Should convert Transaction without completedAt")
    void fromStorageTransaction_pending_restoresCorrectly() {
        var dto = new lv.v3nom.application.dto.storage.TransactionStorageDTO(
                "xyz789transactionId",
                "USD",
                "100.50",
                "333333333333333",
                "444444444444444",
                "DEPOSIT",
                "PENDING",
                "2025-06-15T10:30:00",
                null, // completedAt is null for pending transactions
                null,
                null
        );

        lv.v3nom.domain.model.Transaction restored = StorageMapper.fromStorageTransaction(dto);

        assertEquals("PENDING", restored.getTransactionStatus().getValue());
        assertNull(restored.getCompletedAt());
    }

    @Test
    @DisplayName("Should convert Token to storage DTO")
    void toStorageToken_convertsCorrectly() {
        lv.v3nom.domain.value.CustomerId customerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        LocalDateTime expiry = LocalDateTime.of(2025, 12, 31, 23, 59,01);
        lv.v3nom.domain.value.Token token = lv.v3nom.domain.value.Token.of("tokenValueHere", expiry, customerId);

        var dto = StorageMapper.toStorageToken(token);

        assertEquals("tokenValueHere", dto.getValue());
        assertEquals("2025-12-31T23:59:01", dto.getExpiry());
        assertEquals("123456789012345", dto.getCustomerId());
    }

    @Test
    @DisplayName("Should convert Token storage DTO back to domain model")
    void fromStorageToken_restoresCorrectly() {
        var dto = new lv.v3nom.application.dto.storage.TokenStorageDTO(
                "tokenValueHere",
                "2025-12-31T23:59:00",
                "123456789012345"
        );

        lv.v3nom.domain.value.Token restored = StorageMapper.fromStorageToken(dto);

        assertEquals("tokenValueHere", restored.getValue());
        assertEquals(lv.v3nom.domain.value.CustomerId.of("123456789012345"), restored.getCustomerId());
    }
}
