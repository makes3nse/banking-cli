package lv.v3nom.infrastructure.di;

import java.util.HashMap;
import java.util.Map;

public interface DIContainer {
    Map<Class<?>, Class<?>> dependencies = new HashMap<>();
    <T> void register(Class<T> abstraction, Class<? extends T> implementation);
    <T> void registerInstance(Class<T> abstraction, T instance);
    <T> T resolve(Class<T> type);
}
