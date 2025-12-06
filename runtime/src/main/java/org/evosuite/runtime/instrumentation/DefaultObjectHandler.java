package org.evosuite.runtime.instrumentation;

public class DefaultObjectHandler {
    public Object newInstance(Class<?> type) throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }
}
