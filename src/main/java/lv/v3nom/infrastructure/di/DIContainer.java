package lv.v3nom.infrastructure.di;

import java.util.HashMap;
import java.util.Map;

public interface DIContainer {
    Map<Class<?>, Class<?>> dependencies = new HashMap<>();
    public <T> void register(Class<T> abstraction, Class<? extends T> implementation);
    public <T> T resolve(Class<T> type);
}
