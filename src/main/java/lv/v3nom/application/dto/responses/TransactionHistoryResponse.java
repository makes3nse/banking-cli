package lv.v3nom.application.dto.responses;

import java.util.List;

public class TransactionHistoryResponse {
    private List<TransactionSummaryResponse> transactions;
    private int totalCount;
    private String operationStatus;
    private String errorMessage;


    public TransactionHistoryResponse() {}
    public TransactionHistoryResponse(List<TransactionSummaryResponse> transactions,
                                      int totalCount,
                                      String operationStatus,
                                      String errorMessage) {

        this.transactions = transactions;
        this.totalCount = totalCount;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public List<TransactionSummaryResponse> getTransactions() { return transactions; }
    public int getTotalCount() { return totalCount; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
