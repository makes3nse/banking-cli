package lv.v3nom.domain.model;

import lv.v3nom.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for Account")
class AccountTest {

    private CustomerId customerId;
    private Currency currency;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        customerId = CustomerId.of("123456789012345");
        currency = Currency.EUR;
        now = LocalDateTime.of(2025, 1, 1, 0, 0);
    }

    @Test
    @DisplayName("Should throw exception when owner ID is null")
    void open_nullOwnerId_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                Account.open(null, Currency.EUR, CustomerStatus.ACTIVE, now)
        );
    }

    @Test
    @DisplayName("Should create active account with zero balance when customer is ACTIVE")
    void open_activeCustomer_createsAccount() {
        Account account = Account.open(customerId, Currency.USD, CustomerStatus.ACTIVE, now);

        assertNotNull(account);
        assertEquals(CustomerId.of(account.getOwnerId().getValue()), customerId);
        assertEquals(Currency.USD, account.getCurrency());
        assertEquals(Money.zero(Currency.USD), account.getBalance());
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        assertEquals(now, account.getCreatedAt());
        assertEquals(now, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when customer is not ACTIVE")
    void open_inactiveCustomer_throwsException() {
        assertThrows(IllegalStateException.class, () ->
                Account.open(customerId, Currency.EUR, CustomerStatus.SUSPENDED, now)
        );
    }

    @Test
    @DisplayName("Should reconstitute account from persisted state")
    void reconstitute_restoresAccountState() {
        AccountId accountId = AccountId.of("987654321098765");
        CustomerId owner = CustomerId.of("111111111111111");
        Currency cur = Currency.GBP;
        Money balance = Money.of(BigDecimal.valueOf(500).setScale(2), cur);
        AccountStatus status = AccountStatus.FROZEN;
        LocalDateTime created = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 7, 20, 12, 30);

        Account account = Account.reconstitute(accountId, owner, cur, balance, status, created, updated);

        assertEquals(accountId, account.getAccountId());
        assertEquals(owner, account.getOwnerId());
        assertEquals(cur, account.getCurrency());
        assertEquals(balance, account.getBalance());
        assertEquals(status, account.getAccountStatus());
        assertEquals(created, account.getCreatedAt());
        assertEquals(updated, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should deposit money when status is ACTIVE")
    void deposit_activeAccount_successfullyAddsBalance() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Money depositAmount = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);

        account.deposit(depositAmount, now);

        assertEquals(Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR), account.getBalance());
        assertEquals(now, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when depositing to frozen account")
    void deposit_frozenAccount_throwsException() {
        Account account = Account.open(customerId, Currency.GBP, CustomerStatus.ACTIVE, now);
        account.freeze(now);
        Money depositAmount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);

        assertThrows(IllegalStateException.class, () ->
                account.deposit(depositAmount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when currency mismatch on deposit")
    void deposit_currencyMismatch_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Money wrongCurrencyAmount = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.USD);

        assertThrows(IllegalStateException.class, () ->
                account.deposit(wrongCurrencyAmount, now)
        );
    }

    @Test
    @DisplayName("Should accumulate multiple deposits correctly")
    void deposit_multipleDeposits_accumulatesBalance() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.deposit(Money.of(BigDecimal.valueOf(50).setScale(2), Currency.EUR), now);
        account.deposit(Money.of(BigDecimal.valueOf(30).setScale(2), Currency.EUR), now);

        assertEquals(Money.of(BigDecimal.valueOf(80).setScale(2), Currency.EUR), account.getBalance());
    }

    @Test
    @DisplayName("Should withdraw money when status is ACTIVE and funds sufficient")
    void withdraw_activeAccount_sufficientFunds_success() {
        Account account = Account.open(customerId, Currency.USD, CustomerStatus.ACTIVE, now);
        account.deposit(Money.of(BigDecimal.valueOf(200).setScale(2), Currency.USD), now);

        Money withdrawalAmount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.USD);
        LocalDateTime afterDeposit = now.plusHours(1);
        account.withdraw(withdrawalAmount, afterDeposit);

        assertEquals(Money.of(BigDecimal.valueOf(150).setScale(2), Currency.USD), account.getBalance());
        assertEquals(afterDeposit, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from frozen account")
    void withdraw_frozenAccount_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.freeze(now);
        Money withdrawalAmount = Money.of(BigDecimal.valueOf(10).setScale(2), Currency.EUR);

        assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(withdrawalAmount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when insufficient funds on withdraw")
    void withdraw_insufficientFunds_throwsException() {
        Account account = Account.open(customerId, Currency.GBP, CustomerStatus.ACTIVE, now);
        Money largeWithdrawal = Money.of(BigDecimal.valueOf(1000).setScale(2), Currency.GBP);

        assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(largeWithdrawal, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when currency mismatch on withdraw")
    void withdraw_currencyMismatch_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.deposit(Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR), now);
        Money wrongCurrencyAmount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.USD);

        assertThrows(IllegalStateException.class, () ->
                account.withdraw(wrongCurrencyAmount, now)
        );
    }

    @Test
    @DisplayName("Should transfer money between active accounts")
    void transfer_activeAccounts_success() {
        Account sender = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Account receiver = Account.open(CustomerId.of("222222222222222"), Currency.EUR, CustomerStatus.ACTIVE, now);

        sender.deposit(Money.of(BigDecimal.valueOf(500).setScale(2), Currency.EUR), now);
        Money transferAmount = Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR);
        LocalDateTime afterDeposit = now.plusHours(1);
        LocalDateTime transferTime = now.plusMinutes(30);

        sender.transferToAccount(receiver, transferAmount, transferTime);

        assertEquals(Money.of(BigDecimal.valueOf(400).setScale(2), Currency.EUR), sender.getBalance());
        assertEquals(Money.of(BigDecimal.valueOf(100).setScale(2), Currency.EUR), receiver.getBalance());
        assertEquals(transferTime, sender.getUpdatedAt());
        assertEquals(transferTime, receiver.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when transferring to self")
    void transfer_toSelf_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Money amount = Money.of(BigDecimal.valueOf(10).setScale(2), Currency.EUR);

        assertThrows(IllegalArgumentException.class, () ->
                account.transferToAccount(account, amount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when sender is frozen")
    void transfer_senderFrozen_throwsException() {
        Account sender = Account.open(customerId, Currency.GBP, CustomerStatus.ACTIVE, now);
        Account receiver = Account.open(CustomerId.of("333333333333333"), Currency.GBP, CustomerStatus.ACTIVE, now);

        sender.freeze(now);
        Money amount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.GBP);

        assertThrows(IllegalStateException.class, () ->
                sender.transferToAccount(receiver, amount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when receiver is frozen")
    void transfer_receiverFrozen_throwsException() {
        Account sender = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Account receiver = Account.open(CustomerId.of("444444444444444"), Currency.EUR, CustomerStatus.ACTIVE, now);

        receiver.freeze(now);
        sender.deposit(Money.of(BigDecimal.valueOf(200).setScale(2), Currency.EUR), now);
        Money amount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.EUR);

        assertThrows(IllegalStateException.class, () ->
                sender.transferToAccount(receiver, amount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when insufficient funds on transfer")
    void transfer_insufficientFunds_throwsException() {
        Account sender = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Account receiver = Account.open(CustomerId.of("555555555555555"), Currency.EUR, CustomerStatus.ACTIVE, now);

        Money transferAmount = Money.of(BigDecimal.valueOf(1000).setScale(2), Currency.EUR);

        assertThrows(IllegalArgumentException.class, () ->
                sender.transferToAccount(receiver, transferAmount, now)
        );
    }

    @Test
    @DisplayName("Should throw exception when currency mismatch on transfer")
    void transfer_currencyMismatch_throwsException() {
        Account sender = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        Account receiver = Account.open(CustomerId.of("666666666666666"), Currency.USD, CustomerStatus.ACTIVE, now);

        sender.deposit(Money.of(BigDecimal.valueOf(200).setScale(2), Currency.EUR), now);
        Money amount = Money.of(BigDecimal.valueOf(50).setScale(2), Currency.USD);

        assertThrows(IllegalStateException.class, () ->
                sender.transferToAccount(receiver, amount, now)
        );
    }

    @Test
    @DisplayName("Should close account with zero balance")
    void close_zeroBalance_success() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);

        LocalDateTime closeTime = now.plusHours(1);
        account.close(closeTime);

        assertEquals(AccountStatus.CLOSED, account.getAccountStatus());
        assertEquals(closeTime, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when closing non-zero balance")
    void close_nonZeroBalance_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.deposit(Money.of(BigDecimal.valueOf(10).setScale(2), Currency.EUR), now);

        assertThrows(IllegalStateException.class, () ->
                account.close(now)
        );
    }

    @Test
    @DisplayName("Should throw exception when closing blocked account")
    void close_blockedAccount_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        // Simulate blocked status by reconstituting
        LocalDateTime updated = now.plusHours(1);
        account.block(updated);

        assertThrows(IllegalStateException.class, () ->
                account.close(now)
        );
    }

    @Test
    @DisplayName("Should freeze active account")
    void freeze_activeAccount_success() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);

        LocalDateTime freezeTime = now.plusHours(1);
        account.freeze(freezeTime);

        assertEquals(AccountStatus.FROZEN, account.getAccountStatus());
        assertEquals(freezeTime, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when freezing non-active account")
    void freeze_nonActiveAccount_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.freeze(now);

        assertThrows(IllegalStateException.class, () ->
                account.freeze(now)
        );
    }

    @Test
    @DisplayName("Should unfreeze frozen account")
    void unfreeze_frozenAccount_success() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);
        account.freeze(now);

        LocalDateTime unfreezeTime = now.plusHours(1);
        account.unfreeze(unfreezeTime);

        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        assertEquals(unfreezeTime, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when unfreezing non-frozen account")
    void unfreeze_nonFrozenAccount_throwsException() {
        Account account = Account.open(customerId, Currency.EUR, CustomerStatus.ACTIVE, now);

        assertThrows(IllegalStateException.class, () ->
                account.unfreeze(now)
        );
    }
}
