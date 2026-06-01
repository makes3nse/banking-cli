package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.TransactionDetailsRequest;
import lv.v3nom.application.dto.requests.TransactionHistoryRequest;
import lv.v3nom.application.dto.responses.TransactionHistoryResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;

public interface TransactionService {
    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request);
    public TransactionResponse getTransactionDetails(TransactionDetailsRequest request);
}
