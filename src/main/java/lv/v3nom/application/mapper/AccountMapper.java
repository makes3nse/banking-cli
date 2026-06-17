package lv.v3nom.application.mapper;

import lv.v3nom.application.dto.requests.OpenAccountRequest;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.BalanceResponse;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.Currency;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.OperationStatus;

import java.time.LocalDateTime;

public class AccountMapper {
//    public static Account toDomain(OpenAccountRequest request, Customer owner, LocalDateTime now) {
//        return Account.open(
//                CustomerId.of(request.getCustomerId()),
//                Currency.of(request.getCurrency()),
//                owner.getCustomerStatus(),
//                now);
//    }
    public static AccountResponse toResponse(Account account, OperationStatus operationStatus) {
        return new AccountResponse(
                account.getAccountId().getValue(),
                account.getOwnerId().getValue(),
                account.getAccountStatus().getValue(),
                account.getCurrency().value(),
                account.getBalance().getValue(),
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    public static BalanceResponse toBalanceResponse(Account account, OperationStatus operationStatus) {
        return new BalanceResponse(
                account.getAccountId().getValue(),
                account.getCurrency().value(),
                account.getBalance().getValue(),
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    // helpers
    public static AccountResponse failureResponse(String customerId, OperationStatus status) {
        return new AccountResponse(
                null,
                customerId,
                null,
                null,
                null,
                status.getValue(),
                status.getDescription());
    }
    public static BalanceResponse failureResponseBalance(String failureReason, OperationStatus operationStatus) {
        return new BalanceResponse(
                null,
                null,
                null,
                operationStatus.getValue(),
                failureReason
        );
    }
}
