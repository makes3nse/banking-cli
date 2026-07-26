package lv.v3nom.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AccountMapper")
class AccountMapperTest {

    private lv.v3nom.domain.model.Account account;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 1, 1, 0, 0);
        // Reconstitute an account with known state for testing mappers
        lv.v3nom.domain.value.AccountId accountId = lv.v3nom.domain.value.AccountId.of("987654321098765");
        lv.v3nom.domain.value.CustomerId ownerId = lv.v3nom.domain.value.CustomerId.of("123456789012345");
        lv.v3nom.domain.value.Currency currency = lv.v3nom.domain.value.Currency.EUR;
        lv.v3nom.domain.value.Money balance = lv.v3nom.domain.value.Money.of(BigDecimal.valueOf(500).setScale(2), currency);
        lv.v3nom.domain.value.AccountStatus status = lv.v3nom.domain.value.AccountStatus.ACTIVE;

        account = lv.v3nom.domain.model.Account.reconstitute(
                accountId, ownerId, currency, balance, status, now, now);
    }

    @Test
    @DisplayName("Should convert Account to response with success status")
    void toResponse_accountWithActiveStatus_returnsAccountResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("SUCCESS");

        var response = AccountMapper.toResponse(account, opStatus);

        assertEquals("987654321098765", response.getAccountId());
        assertEquals("123456789012345", response.getCustomerId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("EUR", response.getCurrency());
        assertEquals(BigDecimal.valueOf(500).setScale(2), response.getBalance());
        assertEquals("SUCCESS", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should convert Account to balance response")
    void toBalanceResponse_returnsBalanceResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("SUCCESS");

        var response = AccountMapper.toBalanceResponse(account, opStatus);

        assertEquals("987654321098765", response.getAccountId());
        assertEquals("EUR", response.getCurrency());
        assertEquals(BigDecimal.valueOf(500).setScale(2), response.getBalance());
        assertEquals("SUCCESS", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should create failure response for account")
    void failureResponse_returnsFailureAccountResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = AccountMapper.failureResponse("123456789012345", opStatus);

        assertNull(response.getAccountId());
        assertEquals("123456789012345", response.getCustomerId());
        assertNull(response.getStatus());
        assertNull(response.getCurrency());
        assertNull(response.getBalance());
        assertEquals("FAILURE", response.getOperationStatus());
    }

    @Test
    @DisplayName("Should create failure balance response")
    void failureBalanceResponse_returnsFailureBalanceResponse() {
        lv.v3nom.domain.value.OperationStatus opStatus = lv.v3nom.domain.value.OperationStatus.of("FAILURE");

        var response = AccountMapper.failureResponseBalance("123456789012345", opStatus);

        assertNull(response.getAccountId());
        assertNull(response.getCurrency());
        assertNull(response.getBalance());
        assertEquals("FAILURE", response.getOperationStatus());
        assertEquals("123456789012345", response.getErrorMessage());
    }
}
