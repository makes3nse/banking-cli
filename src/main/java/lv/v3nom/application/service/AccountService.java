package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.AccountStatusResponse;
import lv.v3nom.application.dto.responses.BalanceResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;

import java.util.List;

public interface AccountService {
    AccountResponse openAccount(OpenAccountRequest request);
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    BalanceResponse getBalance(ViewBalanceRequest request);
    List<AccountResponse> getAccountsByCustomer(GetAccountsRequest request);
    AccountStatusResponse closeAccount(CloseAccountRequest request);
    AccountStatusResponse freezeAccount(FreezeAccountRequest request);
    AccountStatusResponse unfreezeAccount(UnfreezeAccountRequest request);
    AccountResponse getAccountDetails(GetAccountDetailsRequest request);
}
