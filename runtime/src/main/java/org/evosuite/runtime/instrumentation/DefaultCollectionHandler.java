package org.evosuite.runtime.instrumentation;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultCollectionHandler {
    public Collection<?> newCollection(Class<?> type) {
        return new ArrayList<>();
    }
}
