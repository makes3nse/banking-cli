package lv.v3nom.infrastructure.idempotency;

// helper VO (response *serialized* JSON + expiry bundle)
public final class IdempotencyEntryJSON {
    // So, it is a serialized JSON object, created through request
    private String response;
    private long expiryTime;

    private IdempotencyEntryJSON(String response, long expiryTime) {
        if (response == null) {
            throw new IllegalArgumentException("Idempotency Entry cannot be created with null values");
        }

        this.response = response;
        this.expiryTime = expiryTime;
    }

    public static IdempotencyEntryJSON of(String response, long expiryTime) {
        return new IdempotencyEntryJSON(response, expiryTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        IdempotencyEntryJSON that = (IdempotencyEntryJSON) o;
        return this.response.equals(that.response);
    }
    @Override
    public int hashCode() {
        return this.response.hashCode();
    }

    public String getResponse() { return response; }
    public long getExpiryTime() { return expiryTime; }
}
