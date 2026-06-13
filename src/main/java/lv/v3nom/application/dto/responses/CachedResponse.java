package lv.v3nom.application.dto.responses;

public class CachedResponse {
    private String responseJson;
    private String responseType;

    public CachedResponse() {}
    public CachedResponse(String responseJson, String responseType) {
        this.responseJson = responseJson;
        this.responseType = responseType;
    }

    public String getResponseJson() { return responseJson; }
    public String getResponseType() { return responseType; }
}
