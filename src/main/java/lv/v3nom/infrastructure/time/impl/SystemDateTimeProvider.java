package lv.v3nom.infrastructure.time.impl;

import lv.v3nom.infrastructure.time.DateTimeProvider;

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
