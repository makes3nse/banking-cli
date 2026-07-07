package lv.v3nom.application.dto.responses;

public class BooleanResponse {
    private boolean value;
    private String errorMessage;

    public BooleanResponse() {}
    public BooleanResponse(boolean value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public boolean value() { return value; }
    public String getErrorMessage() { return errorMessage; }
}
