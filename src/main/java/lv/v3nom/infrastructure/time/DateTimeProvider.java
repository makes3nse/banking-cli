package lv.v3nom.infrastructure.util;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second);
    LocalDateTime now();
}
