package lv.v3nom.domain.value;

public final class ErrorCode {
    private final String value;
    private final int index;

    private ErrorCode(String value, int index) {
        this.value = value;
        this.index = index;
    }

    // 4xx Client Error Codes
    public static final ErrorCode BAD_REQUEST =
            new ErrorCode("BAD_REQUEST", 400);
    public static final ErrorCode UNAUTHORIZED =
            new ErrorCode("UNAUTHORIZED", 401);
    public static final ErrorCode PAYMENT_REQUIRED =
            new ErrorCode("PAYMENT_REQUIRED", 402);
    public static final ErrorCode FORBIDDEN =
            new ErrorCode("FORBIDDEN", 403);
    public static final ErrorCode NOT_FOUND =
            new ErrorCode("NOT_FOUND", 404);
    public static final ErrorCode METHOD_NOT_ALLOWED =
            new ErrorCode("METHOD_NOT_ALLOWED", 405);
    public static final ErrorCode NOT_ACCEPTABLE =
            new ErrorCode("NOT_ACCEPTABLE", 406);
    public static final ErrorCode PROXY_AUTHENTICATION_REQUIRED =
            new ErrorCode("PROXY_AUTHENTICATION_REQUIRED", 407);
    public static final ErrorCode REQUEST_TIMEOUT =
            new ErrorCode("REQUEST_TIMEOUT", 408);
    public static final ErrorCode CONFLICT =
            new ErrorCode("CONFLICT", 409);
    public static final ErrorCode GONE =
            new ErrorCode("GONE", 410);
    public static final ErrorCode LENGTH_REQUIRED =
            new ErrorCode("LENGTH_REQUIRED", 411);
    public static final ErrorCode PRECONDITION_FAILED =
            new ErrorCode("PRECONDITION_FAILED", 412);
    public static final ErrorCode PAYLOAD_TOO_LARGE =
            new ErrorCode("PAYLOAD_TOO_LARGE", 413);
    public static final ErrorCode URI_TOO_LONG =
            new ErrorCode("URI_TOO_LONG", 414);
    public static final ErrorCode UNSUPPORTED_MEDIA_TYPE =
            new ErrorCode("UNSUPPORTED_MEDIA_TYPE", 415);
    public static final ErrorCode RANGE_NOT_SATISFIABLE =
            new ErrorCode("RANGE_NOT_SATISFIABLE", 416);
    public static final ErrorCode EXPECTATION_FAILED =
            new ErrorCode("EXPECTATION_FAILED", 417);
    public static final ErrorCode I_AM_A_TEAPOT =
            new ErrorCode("I_AM_A_TEAPOT", 418);
    public static final ErrorCode UNPROCESSABLE_ENTITY =
            new ErrorCode("UNPROCESSABLE_ENTITY", 422);
    public static final ErrorCode TOO_MANY_REQUESTS =
            new ErrorCode("TOO_MANY_REQUESTS", 429);

    // 5xx Server Error Codes
    public static final ErrorCode INTERNAL_SERVER_ERROR =
            new ErrorCode("INTERNAL_SERVER_ERROR", 500);
    public static final ErrorCode NOT_IMPLEMENTED =
            new ErrorCode("NOT_IMPLEMENTED", 501);
    public static final ErrorCode BAD_GATEWAY =
            new ErrorCode("BAD_GATEWAY", 502);
    public static final ErrorCode SERVICE_UNAVAILABLE =
            new ErrorCode("SERVICE_UNAVAILABLE", 503);
    public static final ErrorCode GATEWAY_TIMEOUT =
            new ErrorCode("GATEWAY_TIMEOUT", 504);
    public static final ErrorCode HTTP_VERSION_NOT_SUPPORTED =
            new ErrorCode("HTTP_VERSION_NOT_SUPPORTED", 505);

    @Override
    public String toString() {
        return value + " (" + index + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorCode that = (ErrorCode) o;
        return this.index == that.index &&
                this.value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }

    public String getValue() { return value; }
    public int getIndex() { return index; }
}