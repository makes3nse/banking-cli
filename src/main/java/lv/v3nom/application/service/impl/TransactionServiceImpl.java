package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.TransactionMapper;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.repository.INMEM.TransactionRepository;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CustomerService customerService;
    private final AuthService authService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountService accountService,
                                  CustomerService customerService,
                                  AuthService authService) {

        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.customerService = customerService;
        this.authService = authService;
    }

    @Override
    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        LocalDateTime fromRange = LocalDateTime.parse(request.getFromDate());
        LocalDateTime toRange = LocalDateTime.parse(request.getToDate());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return new TransactionHistoryResponse(
                    null,
                    0,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        List<Transaction> transactions = transactionRepository.findByDateRangeForAccountId(
                accountId, fromRange, toRange
        );
        List<TransactionSummaryResponse> summaryResponses = new ArrayList<>();

        int transactionCount = 0;
        for (Transaction transaction : transactions) {
            summaryResponses.add(new TransactionSummaryResponse(transaction));
            transactionCount += 1;
        }
        TransactionHistoryResponse response = new TransactionHistoryResponse(
                summaryResponses,
                transactionCount,
                OperationStatus.SUCCESS.getValue(),
                OperationStatus.SUCCESS.getDescription()
        );

        return response;
    }
    @Override
    public TransactionResponse getTransactionDetails(TransactionDetailsRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        TransactionId transactionId = TransactionId.of(request.getTransactionId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse isValidTokenResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = isValidTokenResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return TransactionMapper.failureResponse(
                    null,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }

        Transaction transaction = transactionRepository.findById(transactionId);
        AccountId senderAccountId = transaction.getSourceAccount();
        AccountId receiverAccountId = transaction.getTargetAccount();

        GetAccountDetailsRequest getSenderAccountDetailsRequest = new GetAccountDetailsRequest(
                sessionToken, senderAccountId.getValue()
        );
        AccountResponse senderAccountResponse = accountService.getAccountDetails(getSenderAccountDetailsRequest);
        GetAccountDetailsRequest getReceiverAccountDetailsRequest = new GetAccountDetailsRequest(
                sessionToken, receiverAccountId.getValue()
        );
        AccountResponse receiverAccountResponse = accountService.getAccountDetails(getReceiverAccountDetailsRequest);

        boolean isOwnedByAccount = senderAccountId.equals(accountId) || receiverAccountId.equals(accountId);
        boolean isOwnedByCustomer = authenticatedId.equals(CustomerId.of(senderAccountResponse.getCustomerId()))
                                    || authenticatedId.equals(CustomerId.of(receiverAccountResponse.getCustomerId())
        );
        if (!isOwnedByAccount || !isOwnedByCustomer) {
            String failureReason = String.format(
                    "accountId: %s; isOwnedByAccount: %s; isOwnedByCustomer: %s;",
                    request.getAccountId(),
                    isOwnedByAccount,
                    isOwnedByCustomer
            );

            return TransactionMapper.failureResponse(
                    null,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }

        TransactionResponse response = TransactionMapper.toResponse(transaction, OperationStatus.SUCCESS);

        return response;
    }

    @Override
    public TransactionResponse getFailureResponse(GetFailureTransactionRequest request) {
        TransactionResponse transactionResponse =
                TransactionMapper.failureResponse(
                        TransactionType.of(request.getTransactionType()),
                        request.getFailureReason(),
                        OperationStatus.of(request.getOperationStatus()));
        return transactionResponse;
    }
    @Override
    public TransactionResponse createDepositTransaction(CreateTransactionRequest request) {
        //      What TransactionService should do
        //      -
        //      1.Create Transaction entity
        //      2.Complete it
        //      3.Save to repository
        //      4.Map to TransactionResponse
        //      -
        //      What AccountService should do (after refactor)
        //      -
        //      1.Validate token & account ownership
        //      2.Call transactionService.createDepositTransaction(accountId, amount, idempotencyKey)
        //      3.Update account balance
        //      4.Return response from TransactionService
        try {
            Objects.requireNonNull(request.getTransactionType(), "transactionType");
            Objects.requireNonNull(request.getTransactionId(), "transactionId");
            Objects.requireNonNull(request.getAmount(), "amount");
            Objects.requireNonNull(request.getCurrency(), "currency");
            Objects.requireNonNull(request.getCreatedAt(), "createdAt");
            Objects.requireNonNull(request.getSourceAccountId(), "sourceAccountId");
            Objects.requireNonNull(request.getTargetAccountId(), "targetAccountId");

        } catch (NullPointerException e) {
            String failureReason = String.format("Deposit Request cannot have null values: %s is null", e.getMessage());

            return TransactionMapper.failureResponse(
                    TransactionType.DEPOSIT,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }
        Transaction transaction = Transaction.createDepositRecord(
                TransactionId.of(request.getTransactionId()),
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getTargetAccountId()),
                LocalDateTime.parse(request.getCreatedAt())
        );
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.PROCESSING);
    }
    @Override
    public TransactionResponse createWithdrawTransaction(CreateTransactionRequest request) {
        try {
            Objects.requireNonNull(request.getTransactionType(), "transactionType");
            Objects.requireNonNull(request.getTransactionId(), "transactionId");
            Objects.requireNonNull(request.getAmount(), "amount");
            Objects.requireNonNull(request.getCurrency(), "currency");
            Objects.requireNonNull(request.getCreatedAt(), "createdAt");
            Objects.requireNonNull(request.getSourceAccountId(), "sourceAccountId");
            Objects.requireNonNull(request.getTargetAccountId(), "targetAccountId");

        } catch (NullPointerException e) {
            String failureReason = String.format("Withdrawal Request cannot have null values: %s is null", e.getMessage());

            return TransactionMapper.failureResponse(
                    TransactionType.WITHDRAW,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }
        Transaction transaction = Transaction.createWithdrawalRecord(
                TransactionId.of(request.getTransactionId()),
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getSourceAccountId()),
                LocalDateTime.parse(request.getCreatedAt())
        );
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.PROCESSING);
    }
    @Override
    public TransactionResponse createTransferTransaction(CreateTransactionRequest request) {
        try {
            Objects.requireNonNull(request.getTransactionType(), "transactionType");
            Objects.requireNonNull(request.getTransactionId(), "transactionId");
            Objects.requireNonNull(request.getAmount(), "amount");
            Objects.requireNonNull(request.getCurrency(), "currency");
            Objects.requireNonNull(request.getCreatedAt(), "createdAt");
            Objects.requireNonNull(request.getSourceAccountId(), "sourceAccountId");
            Objects.requireNonNull(request.getTargetAccountId(), "targetAccountId");

        } catch (NullPointerException e) {
            String failureReason = String.format("Transfer Request cannot have null values: %s is null", e.getMessage());

            return TransactionMapper.failureResponse(
                    TransactionType.TRANSFER,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }
        Transaction transaction = Transaction.createTransferRecord(
                TransactionId.of(request.getTransactionId()),
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getSourceAccountId()),
                AccountId.of(request.getTargetAccountId()),
                LocalDateTime.parse(request.getCreatedAt())
        );
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.PROCESSING);
    }
    @Override
    public TransactionResponse completeTransaction(CompleteTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(TransactionId.of(request.getTransactionId()));
        transaction.complete(LocalDateTime.parse(request.getCompletedAt()));
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.SUCCESS);
    }
    @Override
    public TransactionResponse returnTransaction(ReturnTransactionRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        Transaction transaction = transactionRepository.findById(TransactionId.of(request.getTransactionId()));
        CanCustomerManipulateTransactionsRequest canCustomerManipulateTransactionsRequest =
                new CanCustomerManipulateTransactionsRequest(request.getCurrentSessionToken());
        BooleanResponse booleanResponse = customerService.canManipulateTransactions(
                canCustomerManipulateTransactionsRequest
        );
        boolean canReturnTransaction = booleanResponse.value();
        if (!canReturnTransaction) {
            String failureReason = String.format("Insufficient rights to return a transaction");
            TransactionMapper.failureResponse(
                    transaction.getTransactionType(),
                    failureReason,
                    OperationStatus.FAILURE
            );
        }
        transaction.markReturned(time.now(), request.getReturnReason());
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.SUCCESS);
    }
    @Override
    public TransactionResponse rejectTransaction(RejectTransactionRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        Transaction transaction = transactionRepository.findById(TransactionId.of(request.getTransactionId()));
        CanCustomerManipulateTransactionsRequest canCustomerManipulateTransactionsRequest =
                new CanCustomerManipulateTransactionsRequest(request.getCurrentSessionToken());
        BooleanResponse booleanResponse = customerService.canManipulateTransactions(
                canCustomerManipulateTransactionsRequest
        );
        boolean canRejectTransaction = booleanResponse.value();
        if (!canRejectTransaction) {
            String failureReason = String.format("Insufficient rights to reject a transaction");
            TransactionMapper.failureResponse(
                    transaction.getTransactionType(),
                    failureReason,
                    OperationStatus.FAILURE
            );
        }
        transaction.reject(time.now(), request.getRejectReason());
        transactionRepository.save(transaction);

        return TransactionMapper.toResponse(transaction, OperationStatus.SUCCESS);
    }
}
