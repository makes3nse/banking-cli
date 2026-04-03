package lv.v3nom.infrastructure.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DIContainer_Impl implements DIContainer {
    Map<Class<?>, Class<?>> dependencies = new HashMap<>();

    public <T> void register(Class<T> abstraction, Class<? extends T> implementation){
        dependencies.put(abstraction, implementation);
    }

    public <T> T resolve(Class<T> type){
        Class<?> implementation = dependencies.get(type);

        if (implementation == null) {
            implementation = type;
        }

        try {
            Constructor[] constructors = implementation.getDeclaredConstructors();
            Constructor<?> targetConstructor = constructors[0];

            for (Constructor<?> c : constructors) {
                if (c.getParameterCount() > targetConstructor.getParameterCount()) {
                    targetConstructor = c;
                }
            }

            Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
            Object[] dependencyInstances = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                dependencyInstances[i] = resolve(parameterTypes[i]);
            }

            return (T) targetConstructor.newInstance(dependencyInstances);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
