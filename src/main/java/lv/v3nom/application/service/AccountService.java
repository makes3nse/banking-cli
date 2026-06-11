package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.BalanceResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;

import java.util.List;

public interface AccountService {
    public AccountResponse openAccount(OpenAccountRequest request);
    public TransactionResponse deposit(DepositRequest request);
    public TransactionResponse withdraw(WithdrawRequest request);
    public TransactionResponse transfer(TransferRequest request);
    public BalanceResponse getBalance(ViewBalanceRequest request);
    public List<AccountResponse> getAccountsByCustomer(GetAccountsRequest request);
    public AccountResponse closeAccount(CloseAccountRequest request);
    public AccountResponse freezeAccount(FreezeAccountRequest request);
    public AccountResponse unfreezeAccount(UnfreezeAccountRequest request);
    public AccountResponse getAccountDetails(GetAccountDetailsRequest request);
}
