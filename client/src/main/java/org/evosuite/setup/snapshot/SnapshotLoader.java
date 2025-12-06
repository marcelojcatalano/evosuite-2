package org.evosuite.setup.snapshot;

import org.evosuite.setup.InheritanceTree;
import org.evosuite.setup.callgraph.CallGraph;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Final implementation of snapshot loading/saving for EvoSuite.
 * Stores only user classes (no JDK internal classes).
 * Avoids XStream and old JDK8 XML structures.
 */
public final class SnapshotLoader {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotLoader.class);

    private SnapshotLoader() {}

    private static final String BASE_DIR =
            System.getProperty("user.home") + File.separator + ".evosuite" + File.separator + "snapshots";

    private static Path resolve(String jdk, String name) {
        return Path.of(BASE_DIR, jdk, name + ".bin");
    }

    private static boolean isJdkClass(String className) {
        return className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("jdk.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.");
    }

    // ================================================================
    // JDK Version Detection
    // ================================================================
    public static String detectJdkVersion() {
        // normalize e.g. "17.0.8" → "17"
        String ver = System.getProperty("java.specification.version");
        if (ver == null || ver.isBlank()) return "unknown";
        return ver.trim();
    }

    // ================================================================
    // InheritanceTree Snapshot
    // ================================================================
    public static void saveInheritanceSnapshot(String jdkVersion, InheritanceTree tree) {
        try {
            Path file = resolve(jdkVersion, "inheritanceTree");
            Files.createDirectories(file.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                out.writeObject(tree);
            }
            logger.debug("Saved inheritance snapshot for JDK {}", jdkVersion);
        } catch (Exception e) {
            logger.warn("Failed to save inheritance snapshot", e);
        }
    }

    public static InheritanceTree loadInheritanceSnapshot(String jdkVersion) {
        Path file = resolve(jdkVersion, "inheritanceTree");

        if (!Files.exists(file)) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = in.readObject();
            if (obj instanceof InheritanceTree) {
                return (InheritanceTree) obj;
            }
        } catch (Exception e) {
            logger.warn("Failed to load inheritance snapshot", e);
        }
        return null;
    }

    // ================================================================
    // CallGraph Snapshot
    // ================================================================
    public static void saveCallGraph(String className, CallGraph graph) {
        if (isJdkClass(className)) return;

        String jdk = detectJdkVersion();
        try {
            Path file = resolve(jdk, "callgraph_" + className.replace('.', '_'));
            Files.createDirectories(file.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                out.writeObject(graph);
            }
            logger.debug("Saved call graph snapshot for {}", className);
        } catch (Exception e) {
            logger.warn("Failed to save call graph snapshot for " + className, e);
        }
    }

    public static CallGraph loadCallGraph(String className) {
        if (isJdkClass(className)) return null;

        String jdk = detectJdkVersion();
        Path file = resolve(jdk, "callgraph_" + className.replace('.', '_'));

        if (!Files.exists(file)) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = in.readObject();
            if (obj instanceof CallGraph) {
                return (CallGraph) obj;
            }
        } catch (Exception e) {
            logger.warn("Failed to load call graph for " + className, e);
        }

        return null;
    }

    // ================================================================
    // ClassNode Snapshot
    // ================================================================
    public static void saveClassNode(String className, ClassNode node) {
        if (isJdkClass(className)) return;

        String jdk = detectJdkVersion();
        try {
            Path file = resolve(jdk, "classnode_" + className.replace('.', '_'));
            Files.createDirectories(file.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                out.writeObject(node);
            }
            logger.debug("Saved ClassNode snapshot for {}", className);
        } catch (Exception e) {
            logger.warn("Failed to save ClassNode snapshot for " + className, e);
        }
    }

    public static ClassNode loadClassNode(String className) {
        if (isJdkClass(className)) return null;

        String jdk = detectJdkVersion();
        Path file = resolve(jdk, "classnode_" + className.replace('.', '_'));

        if (!Files.exists(file)) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = in.readObject();
            if (obj instanceof ClassNode) {
                return (ClassNode) obj;
            }
        } catch (Exception e) {
            logger.warn("Failed to load ClassNode snapshot for " + className, e);
        }

        return null;
    }
}
