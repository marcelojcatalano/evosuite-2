package org.evosuite.runtime.instrumentation;

import java.util.HashMap;
import java.util.Map;

public class DefaultMapHandler {
    public Map<?, ?> newMap(Class<?> type) {
        return new HashMap<>();
    }
}
