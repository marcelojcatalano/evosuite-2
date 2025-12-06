package org.evosuite.utils;

import java.util.Optional;

public interface InstantiationHandler {
    /**
     * Try to create an instance of the requested class.
     * @param clazz class to instantiate
     * @param <T> type
     * @return Optional containing instance if handler can create one, empty otherwise
     */
    <T> Optional<T> tryInstantiate(Class<T> clazz);
}