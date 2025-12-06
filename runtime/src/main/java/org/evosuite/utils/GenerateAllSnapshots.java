package org.evosuite.utils;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;


/**
 * Generates EvoSuite-style snapshots:
 * - JDK_inheritance.xml
 * - JDK_inheritance_shaded.xml
 * - jdkPureMethods.txt
 * <p>
 * Fully compatible with Java 8–11.
 */
public final class GenerateAllSnapshots {

    private static final List<String> BOOT_MODULES = Arrays.asList(
            "java.base", "java.desktop", "java.logging", "java.xml",
            "java.sql", "java.security.sasl", "java.naming",
            "jdk.compiler", "jdk.unsupported"
    );

    private static final String SHADED_PREFIX = "org.evosuite.shaded.";

    public static void main(String[] args) throws Exception {

        String version = detectJdkVersionString();
        System.out.println(">>> Generating snapshot for JDK " + version);

        Path outputDir = Paths.get("generated-snapshots/jdk-" + version);
        Files.createDirectories(outputDir);

        List<ClassInfo> classes = scanJdkClasses();
        System.out.println("Discovered classes: " + classes.size());

        writeInheritanceXml(classes, outputDir.resolve("JDK_inheritance.xml"));
        writeShadedInheritanceXml(classes, outputDir.resolve("JDK_inheritance_shaded.xml"));
        writePureMethods(classes, outputDir.resolve("jdkPureMethods.txt"));

        System.out.println("\n>>> DONE. Snapshot stored in:");
        System.out.println(outputDir.toAbsolutePath());
    }

    // ------------------------------------------------------------ //
    // VERSION
    // ------------------------------------------------------------ //

    private static String detectJdkVersionString() {
        String v = System.getProperty("java.version");
        return v.replaceAll("[^0-9.]", "").replace('.', '_');
    }

    // ------------------------------------------------------------ //
    // SCAN CLASSES
    // ------------------------------------------------------------ //

    private static List<ClassInfo> scanJdkClasses() throws Exception {

        List<ClassInfo> out = new ArrayList<ClassInfo>();

        Path jmods = Paths.get(System.getProperty("java.home"), "jmods");
        if (!Files.isDirectory(jmods)) {
            throw new RuntimeException("No se encontró el directorio jmods en: " + jmods);
        }

        DirectoryStream<Path> stream = Files.newDirectoryStream(jmods, "*.jmod");
        for (Path mod : stream) {
            processJmod(mod, out);
        }

        return out;
    }

    private static void processJmod(Path jmodPath, List<ClassInfo> result) throws Exception {

        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(jmodPath.toFile())) {

            Enumeration<? extends java.util.zip.ZipEntry> en = zip.entries();

            while (en.hasMoreElements()) {
                java.util.zip.ZipEntry entry = en.nextElement();

                // En jmod, las clases están dentro de /classes/...
                if (!entry.getName().startsWith("classes/") || !entry.getName().endsWith(".class")) {
                    continue;
                }

                InputStream is = zip.getInputStream(entry);
                if (is == null) continue;

                ClassReader cr = new ClassReader(is);
                is.close();

                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                String name = cn.name.replace('/', '.');
                String superName = cn.superName == null ? null : cn.superName.replace('/', '.');

                List<String> ifaces = new ArrayList<String>();
                for (Object o : cn.interfaces) {
                    ifaces.add(((String) o).replace('/', '.'));
                }

                List<String> methods = new ArrayList<String>();
                for (Object mObj : cn.methods) {
                    org.objectweb.asm.tree.MethodNode m = (org.objectweb.asm.tree.MethodNode) mObj;

                    StringBuilder sb = new StringBuilder();
                    sb.append(name).append(".").append(m.name).append("(");

                    Type mt = Type.getMethodType(m.desc);
                    Type[] params = mt.getArgumentTypes();
                    for (int i = 0; i < params.length; i++) {
                        sb.append(params[i].getClassName());
                        if (i < params.length - 1) sb.append(",");
                    }

                    sb.append(")");
                    methods.add(sb.toString());
                }

                result.add(new ClassInfo(name, superName, ifaces, methods));
            }
        }
    }


    private static void processModuleJar(Path jarPath, List<ClassInfo> result) throws Exception {

        JarFile jar = new JarFile(jarPath.toFile());
        Enumeration<?> en = jar.entries();

        while (en.hasMoreElements()) {
            java.util.jar.JarEntry entry = (java.util.jar.JarEntry) en.nextElement();
            if (!entry.getName().endsWith(".class")) continue;

            InputStream is = jar.getInputStream(entry);
            ClassReader cr = new ClassReader(is);
            is.close();

            ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            String name = cn.name.replace('/', '.');
            String superName = cn.superName == null ? null : cn.superName.replace('/', '.');

            List<String> ifaces = new ArrayList<String>();
            for (Object o : cn.interfaces) {
                String iface = ((String) o).replace('/', '.');
                ifaces.add(iface);
            }

            List<String> methods = new ArrayList<String>();
            for (Object mObj : cn.methods) {

                org.objectweb.asm.tree.MethodNode m = (org.objectweb.asm.tree.MethodNode) mObj;

                Type mt = Type.getMethodType(m.desc);
                Type[] params = mt.getArgumentTypes();

                StringBuilder sb = new StringBuilder();
                sb.append(name).append(".").append(m.name).append("(");

                for (int i = 0; i < params.length; i++) {
                    sb.append(params[i].getClassName());
                    if (i < params.length - 1) sb.append(",");
                }

                sb.append(")");
                methods.add(sb.toString());
            }

            result.add(new ClassInfo(name, superName, ifaces, methods));
        }

        jar.close();
    }

    // ------------------------------------------------------------ //
    // WRITE: JDK_inheritance.xml
    // ------------------------------------------------------------ //

    private static void writeInheritanceXml(List<ClassInfo> classes, Path target) throws Exception {

        BufferedWriter w = Files.newBufferedWriter(target, StandardCharsets.UTF_8);

        w.write("<list>\n");
        for (ClassInfo c : classes) {
            w.write("  <object class=\"java.util.ArrayList\">\n");

            w.write("    <string>" + c.name + "</string>\n");
            w.write("    <string>" + (c.superName == null ? "" : c.superName) + "</string>\n");

            for (String iface : c.interfaces) {
                w.write("    <string>" + iface + "</string>\n");
            }

            w.write("  </object>\n");
        }
        w.write("</list>\n");

        w.close();
    }

    // ------------------------------------------------------------ //
    // WRITE: JDK_inheritance_shaded.xml
    // ------------------------------------------------------------ //

    private static void writeShadedInheritanceXml(List<ClassInfo> classes, Path target) throws Exception {

        try (BufferedWriter w = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {

            w.write("<list>\n");

            for (ClassInfo c : classes) {

                w.write("  <object class=\"java.util.ArrayList\">\n");

                w.write("    <string>" + shade(c.name) + "</string>\n");

                if (c.superName == null) {
                    w.write("    <string></string>\n");
                } else {
                    w.write("    <string>" + shade(c.superName) + "</string>\n");
                }

                for (String iface : c.interfaces) {
                    w.write("    <string>" + shade(iface) + "</string>\n");
                }

                w.write("  </object>\n");
            }

            w.write("</list>\n");
        }
    }


    private static boolean isJdkClass(String name) {
        return name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("jdk.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.");
    }

    private static String shade(String name) {
        if (isJdkClass(name)) return name;
        return SHADED_PREFIX + name;
    }


    // ------------------------------------------------------------ //
    // WRITE: jdkPureMethods.txt
    // ------------------------------------------------------------ //

    private static void writePureMethods(List<ClassInfo> classes, Path target) throws Exception {

        BufferedWriter w = Files.newBufferedWriter(target, StandardCharsets.UTF_8);

        for (ClassInfo c : classes) {
            for (String m : c.methods) {
                w.write(m);
                w.write("\n");
            }
        }

        w.close();
    }
}
