package lv.v3nom.infrastructure.idempotency;

import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.IdempotencyKey;

import java.util.HashMap;
import java.util.Map;

public class IdempotencyStore {
    private final Map<String, IdempotencyEntry> idempotencyStore = new HashMap<>();
    private final long ttlMillis;

    public IdempotencyStore(long ttlSeconds) {
        this.ttlMillis = ttlSeconds * 1000;
    }

    public void store(CustomerId customerId, IdempotencyKey idempotencyKey, Object response) {
        String combinedKey = customerId.toString() + ":" + idempotencyKey.toString();
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        idempotencyStore.put(combinedKey, IdempotencyEntry.of(response, expiryTime));
    }
    public Object retrive(CustomerId customerId, IdempotencyKey idempotencyKey) {
        String combinedKey = customerId.toString() + ":" + idempotencyKey.toString();
        IdempotencyEntry entry = idempotencyStore.get(combinedKey);

        if (entry == null) return null;
        if (System.currentTimeMillis() > entry.getExpiryTime()) {
            idempotencyStore.remove(combinedKey);
            return null;
        }

        return entry.getResponse();
    }
}
