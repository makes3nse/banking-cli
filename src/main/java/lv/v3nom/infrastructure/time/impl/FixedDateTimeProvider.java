package lv.v3nom.infrastructure.time.impl;

import lv.v3nom.infrastructure.time.DateTimeProvider;

import java.time.LocalDateTime;

public class FixedDateTimeProvider implements DateTimeProvider {
    private final LocalDateTime fixedTime;

    public FixedDateTimeProvider(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public LocalDateTime now() {
        return fixedTime;
    }
}
