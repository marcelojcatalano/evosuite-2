package org.evosuite.runtime.initialization;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

/**
 * Carga pequeños scripts o archivos de inicialización desde
 * resources/evosuite/runtime/init/
 *
 * Cada archivo puede contener una o más líneas:
 *   ACTION TYPE VALUE
 * Ejemplo:
 *   reset java.lang.StringBuilder
 *   mock java.awt.Component
 */
public final class RuntimeInitializationScripts {

    private RuntimeInitializationScripts() {}

    public static void loadAll() {
        try {
            ClassLoader cl = RuntimeInitializationScripts.class.getClassLoader();
            Enumeration<URL> resources =
                    cl.getResources("evosuite/runtime/init");

            while (resources.hasMoreElements()) {
                URL folder = resources.nextElement();
                loadFromFolder(cl, folder);
            }
        } catch (Exception e) {
            System.err.println("[RuntimeInit] Error loading initialization resources: " + e);
        }
    }

    private static void loadFromFolder(ClassLoader cl, URL folder) {
        try (InputStream in = folder.openStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                reader.lines().forEach(RuntimeInitializationScripts::parseLine);
            }
        } catch (Exception ignored) {
        }
    }

    private static void parseLine(String line) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) return;

        // Ejemplo de formato simple:
        // reset java.lang.StringBuilder
        String[] parts = line.split("\\s+");
        if (parts.length < 2) return;

        String action = parts[0];
        String target = parts[1];

        switch (action.toLowerCase()) {
            case "reset":
                // Podés ampliarlo luego, por ahora solo lo deja registrado.
                System.out.println("[RuntimeInit] Reset: " + target);
                break;

            case "mock":
                System.out.println("[RuntimeInit] Mock: " + target);
                break;

            default:
                System.out.println("[RuntimeInit] Unknown action: " + line);
        }
    }
}
