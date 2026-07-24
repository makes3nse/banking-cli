package lv.v3nom.application.mapper;

import lv.v3nom.application.dto.storage.AccountStorageDTO;
import lv.v3nom.application.dto.storage.CustomerStorageDTO;
import lv.v3nom.application.dto.storage.TokenStorageDTO;
import lv.v3nom.application.dto.storage.TransactionStorageDTO;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.*;

import java.time.LocalDateTime;

public class StorageMapper {
    public static AccountStorageDTO toStorageAccount(Account account) {
        return new AccountStorageDTO(
                account.getAccountId().getValue(),
                account.getOwnerId().getValue(),
                account.getCurrency().value(),
                account.getBalance().getValue().toString(),
                account.getAccountStatus().getValue(),
                account.getCreatedAt().toString(),
                account.getUpdatedAt().toString()
        );
    }
    public static Account fromStorageAccount(AccountStorageDTO accountStorageDTO) {
        try {
            AccountId accountId = AccountId.of(accountStorageDTO.getAccountId());
            CustomerId customerId = CustomerId.of(accountStorageDTO.getOwnerId());
            Currency currency = Currency.of(accountStorageDTO.getCurrency());
            Money balance = Money.of(accountStorageDTO.getBalance(), currency);
            AccountStatus accountStatus = AccountStatus.of(accountStorageDTO.getAccountStatus());
            LocalDateTime createdAt = LocalDateTime.parse(accountStorageDTO.getCreatedAt());
            LocalDateTime updatedAt = LocalDateTime.parse(accountStorageDTO.getUpdatedAt());

            return Account.reconstitute(
                    accountId,
                    customerId,
                    currency,
                    balance,
                    accountStatus,
                    createdAt,
                    updatedAt
            );
        } catch (Exception e) {
            System.err.println("Error reconstituting account: " + e.getMessage());
            throw new RuntimeException("Failed to reconstitute account", e);
        }
    }
    public static CustomerStorageDTO toStorageCustomer(Customer customer) {
        return new CustomerStorageDTO(
                customer.getId().getValue(),
                customer.getRole().toString(),
                customer.getCustomerStatus().value,
                customer.getName(),
                customer.getEmail().getValue(),
                customer.getPhoneNumber().getValue(),
                customer.getPassword().getValue(),
                customer.getCreatedAt().toString(),
                customer.getUpdatedAt().toString()
        );
    }
    public static Customer fromStorageCustomer(CustomerStorageDTO customerStorageDTO, PasswordHasher passwordHasher) {
        try {
            CustomerId customerId = CustomerId.of(customerStorageDTO.getId());
            Role role = Role.valueOf(Role.class, customerStorageDTO.getRole());
            CustomerStatus customerStatus = CustomerStatus.of(customerStorageDTO.getCustomerStatus());
            String name = customerStorageDTO.getName();
            EmailAddress emailAddress = EmailAddress.of(customerStorageDTO.getEmail());
            PhoneNumber phoneNumber = PhoneNumber.of(customerStorageDTO.getPhoneNumber());
            Password hashedPassword = Password.of(customerStorageDTO.getPassword());
            LocalDateTime createdAt = LocalDateTime.parse(customerStorageDTO.getCreatedAt());
            LocalDateTime updatedAt = LocalDateTime.parse(customerStorageDTO.getUpdatedAt());

            return Customer.reconstitute(customerId,
                    role,
                    customerStatus,
                    name,
                    emailAddress,
                    phoneNumber,
                    hashedPassword,
                    passwordHasher,
                    createdAt,
                    updatedAt
            );
        } catch (Exception e) {
            System.err.println("Error reconstituting customer: " + e.getMessage());
            throw new RuntimeException("Failed to reconstitute customer", e);
        }
    }
    public static TransactionStorageDTO toStorageTransaction(Transaction transaction) {
        return new TransactionStorageDTO(
                transaction.getTransactionId().getValue(),
                transaction.getCurrency().value(),
                transaction.getAmount().getValue().toString(),
                transaction.getSourceAccount().getValue(),
                transaction.getTargetAccount().getValue(),
                transaction.getTransactionType().getTransactionName(),
                transaction.getTransactionStatus().getValue(),
                transaction.getCreatedAt().toString(),
                transaction.getCompletedAt() != null ? transaction.getCompletedAt().toString() : null,
                transaction.getReturnReason() != null ? transaction.getReturnReason() : null,
                transaction.getRejectReason() != null ? transaction.getRejectReason() : null
        );
    }
    public static Transaction fromStorageTransaction(TransactionStorageDTO transactionStorageDTO) {
        try {
            TransactionId transactionId = TransactionId.of(transactionStorageDTO.getTransactionId());
            Currency currency = Currency.of(transactionStorageDTO.getCurrency());
            Money amount = Money.of(transactionStorageDTO.getAmount(), currency);
            AccountId sourceAccount = AccountId.of(transactionStorageDTO.getSourceAccount());
            AccountId targetAccount = AccountId.of(transactionStorageDTO.getTargetAccount());
            TransactionType transactionType = TransactionType.of(transactionStorageDTO.getTransactionType());
            TransactionStatus transactionStatus = TransactionStatus.of(transactionStorageDTO.getTransactionStatus());
            System.out.println(transactionStatus.getValue());
            LocalDateTime createdAt = LocalDateTime.parse(transactionStorageDTO.getCreatedAt());
            LocalDateTime completedAt = transactionStorageDTO.getCompletedAt() != null ?
                    LocalDateTime.parse(transactionStorageDTO.getCompletedAt()) : null;
            String returnReason = transactionStorageDTO.getReturnReason();
            String rejectReason = transactionStorageDTO.getRejectReason();

            return Transaction.reconstitute(
                    transactionId,
                    currency,
                    amount,
                    sourceAccount,
                    targetAccount,
                    transactionType,
                    transactionStatus,
                    createdAt,
                    completedAt,
                    returnReason,
                    rejectReason
            );
        } catch (Exception e) {
            System.err.println("Error reconstituting transaction: " + e.getMessage());
            throw new RuntimeException("Failed to reconstitute transaction", e);
        }
    }
    public static TokenStorageDTO toStorageToken(Token token) {
        return new TokenStorageDTO(
                token.getValue(),
                token.getExpiry().toString(),
                token.getCustomerId().getValue()
        );
    }
    public static Token fromStorageToken(TokenStorageDTO tokenStorageDTO) {
        try {
            String tokenValue = tokenStorageDTO.getValue();
            LocalDateTime expiry = LocalDateTime.parse(tokenStorageDTO.getExpiry());
            CustomerId customerId = CustomerId.of(tokenStorageDTO.getCustomerId());

            return Token.of(
                    tokenValue,
                    expiry,
                    customerId
            );
        } catch (Exception e) {
            System.err.println("Error reconstituting token: " + e.getMessage());
            throw new RuntimeException("Failed to reconstitute token", e);
        }
    }
}
