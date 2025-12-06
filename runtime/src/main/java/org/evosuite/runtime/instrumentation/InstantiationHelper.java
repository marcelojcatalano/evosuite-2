package org.evosuite.runtime.instrumentation;

import java.util.ArrayList;
import java.util.List;

public final class InstantiationHelper {

    private static final InstantiationHelper INSTANCE = new InstantiationHelper();

    private final List<Object> handlerFactories = new ArrayList<>();

    private InstantiationHelper() { }

    public static InstantiationHelper getInstance() {
        return INSTANCE;
    }

    /**
     * Carga handlers por defecto.
     * Esto reemplaza la lectura de XML del JDK.
     */
    public void loadDefaults() {
        handlerFactories.clear();

        handlerFactories.add(new DefaultArrayHandler());
        handlerFactories.add(new DefaultCollectionHandler());
        handlerFactories.add(new DefaultMapHandler());
        handlerFactories.add(new DefaultEnumHandler());
        handlerFactories.add(new DefaultObjectHandler());
    }

    public List<Object> getHandlerFactories() {
        return handlerFactories;
    }
}
