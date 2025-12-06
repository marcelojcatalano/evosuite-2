package org.evosuite.runtime;

import org.evosuite.runtime.classhandling.ClassStateSupport;
import org.evosuite.runtime.instrumentation.InstantiationHelper;
import org.evosuite.runtime.instrumentation.MethodCallReplacementCache;
import org.evosuite.runtime.mock.MockFramework;
import org.evosuite.runtime.util.ReflectionHelper;

/**
 * Contexto ligero para inicializar handlers, reflexión segura y caches.
 * Suficiente tanto para ClientProcess (initFull) como para JUnitRunner (initLite).
 */
public final class EvoContext {

    private static final EvoContext INSTANCE = new EvoContext();

    private boolean liteInitialized = false;
    private boolean fullInitialized = false;

    private EvoContext() {}

    public static EvoContext get() {
        return INSTANCE;
    }

    /** Inicialización usada por el JUnitRunner */
    public synchronized void initLite() {
        if (liteInitialized) return;

        // 1) Reflection seguro
        ReflectionHelper.initialize();

        // 2) Handlers mínimos para newInstance()
        InstantiationHelper.getInstance().loadDefaults();

        // 3) Mock framework
        MockFramework.enable();

        // 4) Limpieza de reemplazos de métodos (evita NPE)
        MethodCallReplacementCache.resetSingleton();

        liteInitialized = true;
    }

    /** Inicialización usada por el ClientProcess */
    public synchronized void initFull() {
        if (fullInitialized) return;

        // Siempre incluye lo liviano
        initLite();

        // 5) Reset de estados JDK (colecciones, StringBuilders, etc)
        ClassStateSupport.initialize();

        // 6) TODO: agregar aquí si luego activamos:
        //    - CallGraphGenerator.load()
        //    - InheritanceTree.load()

        fullInitialized = true;
    }
}
