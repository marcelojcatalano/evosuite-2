package org.evosuite.utils;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstanceOf(Class<T> c) {
        return InstantiationFactory.newInstance(c);
    }
}
