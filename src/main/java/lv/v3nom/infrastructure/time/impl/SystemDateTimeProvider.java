package lv.v3nom.infrastructure.util.impl;

import lv.v3nom.infrastructure.util.DateTimeProvider;

import java.time.LocalDateTime;

public class SystemDateTimeProvider implements DateTimeProvider {
    @Override
    public LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
