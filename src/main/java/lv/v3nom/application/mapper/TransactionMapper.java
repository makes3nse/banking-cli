package lv.v3nom.application.mapper;

import lv.v3nom.application.dto.requests.DepositRequest;
import lv.v3nom.application.dto.requests.TransferRequest;
import lv.v3nom.application.dto.requests.WithdrawRequest;
import lv.v3nom.application.dto.responses.TransactionHistoryResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;
import lv.v3nom.application.dto.responses.TransactionSummaryResponse;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.*;

import java.time.LocalDateTime;

public class TransactionMapper {
    public static Transaction toDomain(DepositRequest request,
                                       TransactionId transactionId,
                                       LocalDateTime now) {

        return Transaction.createDepositRecord(
                transactionId,
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getAccountId()),
                now
        );
    }
    public static Transaction toDomain(TransferRequest request,
                                       TransactionId transactionId,
                                       LocalDateTime now) {

        return Transaction.createTransferRecord(
                transactionId,
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getSourceAccountId()),
                AccountId.of(request.getTargetAccountId()),
                now
        );
    }
    public static Transaction toDomain(WithdrawRequest request,
                                       TransactionId transactionId,
                                       LocalDateTime now) {

        return Transaction.createWithdrawalRecord(
                transactionId,
                Money.of(request.getAmount(), Currency.of(request.getCurrency())),
                AccountId.of(request.getAccountId()),
                now
        );
    }

    public static TransactionResponse toResponse(Transaction transaction, OperationStatus operationStatus) {
        return new TransactionResponse(
                transaction.getTransactionId().getValue(),
                transaction.getTransactionType().getTransactionName(),
                transaction.getTransactionStatus().getValue(),
                transaction.getSourceAccount().getValue(),
                transaction.getTargetAccount().getValue(),
                transaction.getCurrency().toString(),
                transaction.getAmount().getValue(),
                transaction.getFailureReason(),
                transaction.getCreatedAt().toString(),
                transaction.getCompletedAt() != null ? transaction.getCompletedAt().toString() : null,
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    public static TransactionSummaryResponse toSummaryResponse(Transaction transaction) {
        return new TransactionSummaryResponse(
                transaction
        );
    }
    public static TransactionHistoryResponse toHistoryResponse(Transaction transaction, int count) {
        return new TransactionHistoryResponse(
                //  REDUNDANT.
                //  This process should occur in the TransactionService,
                //  we're going to call there a repository to get list of transactions,
                //  then we're going to loop through list *while counting iterations*
                //  and call this static 'toSummaryResponse()' method from this mapper
                //  then in same service layer we're going to instantiate
                //  new TransactionHistory response DTO and pass everything needed.
        );
    }
    // helpers
    public static TransactionResponse failureResponse(TransactionType transactionType,
                                                      String failureReason,
                                                      OperationStatus operationStatus) {

        return new TransactionResponse(
                null,
                transactionType != null ? transactionType.getTransactionCode() : null,
                null,
                null,
                null,
                null,
                null,
                failureReason,
                null,
                null,
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
}
