package lv.v3nom.application.dto.responses;

public class BooleanResponse {
    private boolean value;

    public BooleanResponse() {}
    public BooleanResponse(boolean value) {
        this.value = value;
    }

    public boolean value() { return value; }
}
