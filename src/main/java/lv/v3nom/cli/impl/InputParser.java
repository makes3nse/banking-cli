package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.requests.*;

// Transformation
public class InputParser {
    // TODO auth
    public RegisterCustomerRequest parseRegister(String name, String email, String phone, String password) {
        // Generate Idempotency
        // return complete request
        return null;
    }
    public LogInRequest parseLogin(String email, String password) {
        // Generate Idempotency
        // return LogInRequest(idempotencyKey, email, password)
        return null;
    }
    public LogOutRequest parseLogout(String token) {
        // Generate Idempotency
        // return LogOutRequest(token, idempotencyKey)
        return null;
    }

    // TODO account related
    public OpenAccountRequest parseOpenAccount(String token, String currency) {
        // Idempotency
        // return OpenAccountRequest(token, idempotencyKey, currency)
        return null;
    }
    public CloseAccountRequest parseCloseAccount(String token, String accountId) {
        // Idempotency
        // return CloseAccountRequest(token, idempotencyKey, accountId)
        return null;
    }
    public FreezeAccountRequest parseFreezeAccount(String token, String accountId) {
        // Idempotency
        // return FreezeAccountRequest(token, idempotencyKey, accountId)
        return null;
    }
    public UnfreezeAccountRequest parseUnfreezeAccount(String token, String accountId) {
        // Idempotency
        // return UnfreezeAccountRequest(token, idempotencyKey, accountId)
        return null;
    }
    public GetAccountDetailsRequest parseGetAccountDetails(String token, String accountId) {
        // no Idempotency
        // return  GetAccountDetailsRequest(token, accountId)
        return null;
    }
    public GetAccountsRequest parseGetAccounts(String token, String customerId) {
        // no Idempotency, read
        // return GetAccountsRequest(token)
        return null;
    }

    // TODO financial
    public DepositRequest parseDeposit(String token, String accountId, String amount, String currency) {
        // Idempotency
        // return DepositRequest(token, idempotencyKey, accountId, currency, amount)
        return null;
    }
    public WithdrawRequest parseWithdraw(String token, String accountId, String amount, String currency) {
        // Idempotency
        // return WithdrawRequest(token, idempotencyKey, accountId, currency, amount)
        return null;
    }
    public TransferRequest parseTransfer(String token, String sourceAccountId, String targetAccountId, String amount, String currency) {
        // Idempotency
        // return TransferRequest(token, idempotencyKey, sourceAccountId, targetAccountId, currency, amount)
        return null;
    }
    public ViewBalanceRequest parseViewBalance(String token, String accountId) {
        // no Idempotency,read
        // return ViewBalanceRequest(token, accountId)
        return null;
    }

    // TODO Transaction History
    public TransactionHistoryRequest parseTransactionHistory(String token, String accountId, String fromDate, String toDate) {
        // no Idempotency
        // validate date format if provided
        // return TransactionHistoryRequest(token, accountId, fromDate, toDate)
        return null;
    }
    public TransactionDetailsRequest parseTransactionDetails(String token, String transactionId) {
        // no Idempotency
        // return TransactionDetailsRequest(String token, String transactionId)
        return null;
    }

    // TODO Customer Profile
    public ChangeNameRequest parseChangeName(String token, String currentName, String newName) {
        //  Idempotency
        // Return ChangeNameRequest(token, idempotencyKey, currentName, newName)
        return null;
    }

    public ChangeEmailRequest parseChangeEmail(String token, String newEmail) {
        //  Idempotency
        // Return ChangeEmailRequest(token, idempotencyKey, newEmail)
        return null;
    }

    public ChangePhoneNumberRequest parseChangePhone(String token, String newPhone) {
        // Idempotency
        // Return ChangePhoneNumberRequest(token, idempotencyKey, newPhone)
        return null;
    }

    public ChangePasswordRequest parseChangePassword(String token, String oldPwd, String newPwd) {
        //  Idempotency
        // Return ChangePasswordRequest(token, idempotencyKey, oldPwd, newPwd)
        return null;
    }

    public GetCustomerRequest parseGetCustomer(String token) {
        // no idempotency, read
        // Return GetCustomerRequest(token)
        return null;
    }
}
