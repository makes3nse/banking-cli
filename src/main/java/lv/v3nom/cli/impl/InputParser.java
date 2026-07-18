package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.domain.value.IdempotencyKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Transformation
public class InputParser {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // auth
    public RegisterCustomerRequest parseRegister(String name, String email, String phone, String password) {
        return new RegisterCustomerRequest(
                IdempotencyKey.generate().getValue(),
                name,
                email,
                phone,
                password
        );
    }
    public LogInRequest parseLogin(String email, String password) {
        return new LogInRequest(
                IdempotencyKey.generate().getValue(),
                email,
                password
        );
    }
    public LogOutRequest parseLogout(String token) {
        return new LogOutRequest(
                token,
                IdempotencyKey.generate().getValue()
        );
    }

    // account related
    public OpenAccountRequest parseOpenAccount(String token, String currency) {
        return new OpenAccountRequest(
                token,
                IdempotencyKey.generate().getValue(),
                currency
        );
    }
    public CloseAccountRequest parseCloseAccount(String token, String accountId) {
        return new CloseAccountRequest(
                token,
                IdempotencyKey.generate().getValue(),
                accountId
        );
    }
    public FreezeAccountRequest parseFreezeAccount(String token, String accountId) {
        return new FreezeAccountRequest(
                token,
                IdempotencyKey.generate().getValue(),
                accountId
        );
    }
    public UnfreezeAccountRequest parseUnfreezeAccount(String token, String accountId) {
        return new UnfreezeAccountRequest(
                token,
                IdempotencyKey.generate().getValue(),
                accountId
        );
    }
    public GetAccountDetailsRequest parseGetAccountDetails(String token, String accountId) {
        return new GetAccountDetailsRequest(
                token,
                accountId
        );
    }
    public GetAccountsRequest parseGetAccounts(String token, String customerId) {
        return new GetAccountsRequest(
                token, customerId
        );
    }

    // financial
    public DepositRequest parseDeposit(String token, String accountId, String amount, String currency) {
        return new DepositRequest(
                token,
                IdempotencyKey.generate().getValue(),
                accountId,
                currency,
                new BigDecimal(amount)
        );
    }
    public WithdrawRequest parseWithdraw(String token, String accountId, String amount, String currency) {
        return new WithdrawRequest(
                token,
                IdempotencyKey.generate().getValue(),
                accountId,
                currency,
                new BigDecimal(amount)
        );
    }
    public TransferRequest parseTransfer(String token, String sourceAccountId, String targetAccountId, String amount, String currency) {
        return new TransferRequest(
                token,
                IdempotencyKey.generate().getValue(),
                sourceAccountId,
                targetAccountId,
                currency,
                new BigDecimal(amount)
        );
    }
    public ViewBalanceRequest parseViewBalance(String token, String accountId) {
        return new ViewBalanceRequest(
                token,
                accountId
        );
    }

    // Transaction History
    public TransactionHistoryRequest parseTransactionHistory(String token, String accountId, String fromDate, String toDate) {
        return new TransactionHistoryRequest(
                token,
                accountId,
                LocalDateTime.parse(fromDate, INPUT_FORMATTER).toString(),
                LocalDateTime.parse(toDate, INPUT_FORMATTER).toString()
        );
    }
    public TransactionDetailsRequest parseTransactionDetails(String token, String accountId, String transactionId) {
        return new TransactionDetailsRequest(
                token,
                accountId,
                transactionId
        );
    }

    // Customer Profile
    public ChangeNameRequest parseChangeName(String token, String currentName, String newName) {
        return new ChangeNameRequest(
                token,
                IdempotencyKey.generate().getValue(),
                currentName,
                newName
        );
    }

    public ChangeEmailRequest parseChangeEmail(String token, String currentEmail, String newEmail) {
        return new ChangeEmailRequest(
                token,
                IdempotencyKey.generate().getValue(),
                currentEmail,
                newEmail
        );
    }

    public ChangePhoneNumberRequest parseChangePhone(String token, String currentPhone, String newPhone) {
        return new ChangePhoneNumberRequest(
                token,
                IdempotencyKey.generate().getValue(),
                currentPhone,
                newPhone
        );
    }

    public ChangePasswordRequest parseChangePassword(String token, String oldPwd, String newPwd) {
        return new ChangePasswordRequest(
                token,
                IdempotencyKey.generate().getValue(),
                oldPwd,
                newPwd
        );
    }

    public GetCustomerRequest parseGetCustomer(String token) {
        return new GetCustomerRequest(token);
    }
}
