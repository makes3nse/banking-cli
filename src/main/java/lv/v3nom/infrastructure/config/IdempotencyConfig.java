package lv.v3nom.infrastructure.config;

import lv.v3nom.infrastructure.time.TimeRules;

public class IdempotencyConfig {
    private final long time;

    public IdempotencyConfig() {
        this.time = TimeRules.M_FIVE_MINUTES; //millis
    }

    public long getTime() {
        return time;
    }
}
