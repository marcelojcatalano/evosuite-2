package org.evosuite.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InstantiationFactory {

    private static final List<InstantiationHandler> handlers = new ArrayList<>();

    private InstantiationFactory() {}

    public static void registerHandler(InstantiationHandler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }

    public static <T> T newInstance(Class<T> cls) {
        // 1) Ask handlers first
        synchronized (handlers) {
            for (InstantiationHandler h : handlers) {
                try {
                    Optional<T> o = h.tryInstantiate(cls);
                    if (o != null && o.isPresent()) {
                        return o.get();
                    }
                } catch (Throwable t) {
                    // handler failed: log and continue to next
                    // use logger if available:
                    System.err.println("InstantiationHandler threw for " + cls.getName() + ": " + t);
                }
            }
        }

        // 2) Try standard reflective instantiation for non-JDK restricted classes
        try {
            Constructor<T> cons = cls.getDeclaredConstructor();
            // allow instantiation of private constructors of *non-JDK modules*:
            try {
                cons.setAccessible(true);
            } catch (SecurityException se) {
                // If the runtime forbids setAccessible, we still try to call the constructor
            }
            return cons.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor for class " + cls.getName(), e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot instantiate class " + cls.getName(), e);
        }
    }
}
