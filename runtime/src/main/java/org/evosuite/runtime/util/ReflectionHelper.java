package org.evosuite.runtime.util;

public class ReflectionHelper {

    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) return;

        // Si tenés utilidades propias de reflexión, cargalas acá.
        // Por ahora se deja vacío como stub.
        initialized = true;
    }
}
