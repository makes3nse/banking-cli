package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;

import java.util.List;

public interface AccountService {
    AccountResponse openAccount(OpenAccountRequest request);
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    BalanceResponse getBalance(ViewBalanceRequest request);
    AccountListResponse getAccountsByCustomer(GetAccountsRequest request);
    AccountStatusResponse closeAccount(CloseAccountRequest request);
    AccountStatusResponse freezeAccount(FreezeAccountRequest request);
    AccountStatusResponse unfreezeAccount(UnfreezeAccountRequest request);
    AccountResponse getAccountDetails(GetAccountDetailsRequest request);
}
