package lv.v3nom.application.mapper;

import lv.v3nom.application.dto.requests.OpenAccountRequest;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.CustomerId;

import java.time.LocalDateTime;

public class AccountMapper {
    public static Account toDomain(OpenAccountRequest request, Customer owner, LocalDateTime now) {
        return Account.open(
                CustomerId.of(request.getCustomerId()),
                owner.getCustomerStatus(),
                now);
    }

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getAccountId().getValue(),
                account.getOwnerId().getValue(),
                account.getAccountStatus().getValue(),
                account.getBalance().getAmount()
        );
    }
}
