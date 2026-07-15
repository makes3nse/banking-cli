package lv.v3nom.application.dto.responses;

import java.util.List;

public class AccountListResponse {
    private List<AccountResponse> accountResponses;
    private int totalCount;
    private String operationStatus;
    private String errorMessage;

    public AccountListResponse() {}
    public AccountListResponse(List<AccountResponse> accountResponses,
                               int totalCount,
                               String operationStatus,
                               String errorMessage) {

        this.accountResponses = accountResponses;
        this.totalCount = totalCount;
        this.operationStatus = operationStatus;
        this.errorMessage = errorMessage;
    }

    public List<AccountResponse> getAccountResponses() { return accountResponses; }
    public int getTotalCount() { return totalCount; }
    public String getOperationStatus() { return operationStatus; }
    public String getErrorMessage() { return errorMessage; }
}
