package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.BooleanResponse;
import lv.v3nom.application.dto.responses.CustomerResponse;
import lv.v3nom.application.dto.responses.TransactionHistoryResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;
import lv.v3nom.application.mapper.TransactionMapper;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.repository.INMEM.TransactionRepository;
import lv.v3nom.infrastructure.security.PermissionChecker;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CustomerService customerService) {

        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
    }

    @Override
    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request) {
        return null;
    }
    @Override
    public TransactionResponse getTransactionDetails(TransactionDetailsRequest request) {
        return null;
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
//    TODO
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
    @Override
    public TransactionResponse createDepositTransaction(CreateTransactionRequest request) {
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
