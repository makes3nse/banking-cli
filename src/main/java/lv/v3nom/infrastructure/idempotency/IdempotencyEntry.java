package lv.v3nom.infrastructure.idempotency;

// helper VO (response + expiry bundle)
public final class IdempotencyEntry {
    // Redundant, Object store cannot be used outside of Monolith microservices project,
    // So, to make things easier we are going to start using right approach straight away
    // It's going to be much easier to do it right now than refactor it later.

//    private Object response;
//    private long expiryTime;
//
//    private IdempotencyEntry(Object response, long expiryTime) {
//        if (response == null) {
//            throw new IllegalArgumentException("Idempotency Entry cannot be created with null values");
//        }
//
//        this.response = response;
//        this.expiryTime = expiryTime;
//    }
//
//    public static IdempotencyEntry of(Object respose, long expiryTime) {
//        return new IdempotencyEntry(respose, expiryTime);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || o.getClass() != this.getClass()) return false;
//        IdempotencyEntry that = (IdempotencyEntry) o;
//        return this.response.equals(that.response);
//    }
//    @Override
//    public int hashCode() {
//        return this.response.hashCode();
//    }
//
//    public Object getResponse() { return response; }
//    public long getExpiryTime() { return expiryTime; }
}
