package org.evosuite.runtime.instrumentation;

public class DefaultEnumHandler {
    public Object firstConstant(Class<?> enumType) {
        return enumType.getEnumConstants()[0];
    }
}
