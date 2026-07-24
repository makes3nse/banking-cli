package lv.v3nom.infrastructure.di.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DIContainerImpl implements lv.v3nom.infrastructure.di.DIContainer {
    Map<Class<?>, Class<?>> dependencies = new HashMap<>();
    Map<Class<?>, Object> instances = new HashMap<>();

    @Override
    public <T> void register(Class<T> abstraction, Class<? extends T> implementation){
        dependencies.put(abstraction, implementation);
    }
    @Override
    public <T> void registerInstance(Class<T> abstraction, T instance) {
        instances.put(abstraction, instance);
    }
    @Override
    public <T> T resolve(Class<T> type){
        return resolveWithTracking(type, new HashSet<>());
    }
    public <T> T resolveWithTracking(Class<T> type, Set<Class<?>> resolvingStack) {
        if (instances.containsKey(type)) {
            return (T) instances.get(type);
        }
        if (resolvingStack.contains(type)) {
            throw new IllegalStateException(
                    String.format(
                            "ERR: Circular Dependency, path: %s",
                            buildCircularPath(resolvingStack, type)
                    )
            );
        }

        resolvingStack.add(type);

        try {
            Class<?> implementation = dependencies.get(type);

            if (implementation == null) {
                implementation = type;
            }
            //System.out.println("Resolving: " + type.getName() + " -> Implementation: " + implementation.getName());
            //System.out.println("Available constructors for " + implementation.getName() + ":");
            Constructor[] constructors = implementation.getDeclaredConstructors();
            Constructor<?> targetConstructor = constructors[0];

            for (Constructor<?> c : constructors) {
                //System.out.println("  " + c + " (params: " + c.getParameterCount() + ")");
                if (c.getParameterCount() > targetConstructor.getParameterCount()) {
                    targetConstructor = c;
                }
            }

            //System.out.println("Selected constructor: " + targetConstructor);

            targetConstructor.setAccessible(true);

            Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
            Object[] dependencyInstances = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> paramClass = parameterTypes[i];
                //System.out.println("  Resolving dependency for param " + i + ": " + paramClass.getName());
                if (paramClass == Supplier.class) {
                    ParameterizedType pType = (ParameterizedType) targetConstructor.getParameters()[i].getParameterizedType();
                    Class<?> depClass = (Class<?>) pType.getActualTypeArguments()[0];
                    dependencyInstances[i] = (Supplier<?>) () ->
                            resolveWithTracking(depClass, resolvingStack);
                } else {
                    dependencyInstances[i] = resolveWithTracking(paramClass, resolvingStack);
                }
            }

            T instance = (T) targetConstructor.newInstance(dependencyInstances);
            resolvingStack.remove(type);

            // we don't need to reset accessibility, because it can mess public constructors
            // and more than that, this setAccessible() flag is not persistent across different invocations
            // so it's safe to leave it in it's overridden 'true' state
            // targetConstructor.setAccessible(false);

            return instance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR resolving " + type.getName());
            resolvingStack.remove(type);
            throw new RuntimeException("Failed to create instance of " + type.getName(), e);
        }
    }

    private String buildCircularPath(Set<Class<?>> resolvingStack, Class<?> circularType) {
        StringBuilder path = new StringBuilder();

        for (Class<?> clazz : resolvingStack) {
            path.append(clazz.getSimpleName()).append(" -> ");
        }
        path.append(circularType.getSimpleName());

        return path.toString();
    }
}
