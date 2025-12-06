package org.evosuite.runtime.instrumentation;

public class DefaultArrayHandler {
    public Object newArray(Class<?> type, int length) {
        return java.lang.reflect.Array.newInstance(type.getComponentType(), length);
    }
}
