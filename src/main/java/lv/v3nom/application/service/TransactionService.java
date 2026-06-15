package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.TransactionHistoryResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;

public interface TransactionService {
    TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request);
    TransactionResponse getTransactionDetails(TransactionDetailsRequest request);
    TransactionResponse getFailureResponse(GetFailureTransactionRequest request);
    TransactionResponse createDepositTransaction(CreateTransactionRequest request);
    TransactionResponse createWithdrawTransaction(CreateTransactionRequest request);
    TransactionResponse createTransferTransaction(CreateTransactionRequest request);
    TransactionResponse completeTransaction(CompleteTransactionRequest request);
    TransactionResponse returnTransaction(CompleteTransactionRequest request);
    TransactionResponse rejectTransaction(CompleteTransactionRequest request);
}
