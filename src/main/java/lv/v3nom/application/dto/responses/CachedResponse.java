package lv.v3nom.application.dto.responses;

public class CachedResponse {
    private String responseJson;
    private String responseType;
    private String errorMessage;

    public CachedResponse() {}
    public CachedResponse(String responseJson, String responseType, String errorMessage) {
        this.responseJson = responseJson;
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }

    public String getResponseJson() { return responseJson; }
    public String getResponseType() { return responseType; }
    public String getErrorMessage() { return errorMessage; }
}
