package org.evosuite.utils;

import java.lang.reflect.Method;

public final class MethodUtils {

    private MethodUtils() {}

    public static Method findMethod(Class<?> type, String name, Class<?>... params) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(name, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }

    public static Object invoke(Object target, String methodName, Class<?>[] params, Object... args) {
        Method m = findMethod(target.getClass(), methodName, params);
        if (m == null)
            throw new RuntimeException("Method not found: " + methodName);

        try { return m.invoke(target, args); }
        catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
}
