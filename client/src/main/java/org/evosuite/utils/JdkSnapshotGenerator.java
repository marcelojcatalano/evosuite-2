package org.evosuite.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.evosuite.setup.InheritanceTree;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JdkSnapshotGenerator {

    // === REGLAS EXACTAS DEL MAVEN SHADE PLUGIN DE EVOSUITE ===
    private static final List<RelocationRule> SHADE_RULES = List.of(
            new RelocationRule("org.objectweb.asm", "org.evosuite.shaded.org.objectweb.asm"),
            new RelocationRule("org.apache.commons.logging", "org.evosuite.shaded.org.apache.commons.logging"),
            new RelocationRule("javassist", "org.evosuite.shaded.javassist"),
            new RelocationRule("antlr", "org.evosuite.shaded.antlr"),
            new RelocationRule("org.dom4j", "org.evosuite.shaded.org.dom4j"),
            new RelocationRule("org.aopalliance", "org.evosuite.shaded.org.aopalliance"),
            new RelocationRule("org.mockito", "org.evosuite.shaded.org.mockito"),
            new RelocationRule("net.bytebuddy", "org.evosuite.shaded.net.bytebuddy"),
            new RelocationRule("javax.servlet", "org.evosuite.shaded.javax.servlet"),
            new RelocationRule("org.apache", "org.evosuite.shaded.org.apache",
                    Set.of("org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter",
                            "org.apache.tools.ant.taskdefs.optional.junit.JUnitTest",
                            "org.apache.tools.ant.BuildException")),
            new RelocationRule("org.hamcrest", "org.evosuite.shaded.org.hamcrest"),
            new RelocationRule("org.objenesis", "org.evosuite.shaded.org.objenesis")
    );

    public static void main(String[] args) throws Exception {
        new JdkSnapshotGenerator().run();
    }

    public void run() throws Exception {

        String version = detectJdkVersionString();
        Path outDir = Paths.get("generated-snapshots", "jdk-" + version);
        Files.createDirectories(outDir);

        System.out.println(">>> Generating snapshot for JDK " + version);

        InheritanceTree tree = new InheritanceTree();
        List<String> pureMethods = new ArrayList<>();

        scanJmodDirectory(tree, pureMethods);

        // ------------------------------
        // GENERAR SIN SOMBREAR
        // ------------------------------
        Path xml = outDir.resolve("JDK_inheritance.xml");
        writeInheritanceXml(tree, xml);

        // ------------------------------
        // GENERAR SOMBREADO
        // ------------------------------
        Path shaded = outDir.resolve("JDK_inheritance_shaded.xml");
        String base = Files.readString(xml, StandardCharsets.UTF_8);
        Files.writeString(shaded, applyShadeRules(base), StandardCharsets.UTF_8);

        // ------------------------------
        // MÉTODOS PUROS
        // ------------------------------
        Collections.sort(pureMethods);
        Path pureTxt = outDir.resolve("jdkPureMethods.txt");
        try (BufferedWriter w = Files.newBufferedWriter(pureTxt, StandardCharsets.UTF_8)) {
            for (String pm : pureMethods) {
                w.write(pm);
                w.newLine();
            }
        }

        System.out.println(">>> Snapshot generated successfully in " + outDir);
    }

    // =====================================================================================
    // HELPERS
    // =====================================================================================

    private static String detectJdkVersionString() {
        String v = System.getProperty("java.version", "");
        return v.replaceAll("[^0-9.]", "").replace('.', '_');
    }

    private void scanJmodDirectory(InheritanceTree tree, List<String> pureMethods) throws IOException {
        Path jmods = Paths.get(System.getProperty("java.home"), "jmods");

        if (!Files.isDirectory(jmods))
            throw new IllegalStateException("No se encontró el directorio jmods: " + jmods);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(jmods, "*.jmod")) {
            for (Path p : stream) {
                processJmod(p, tree, pureMethods);
            }
        }
    }

    private void processJmod(Path jmod, InheritanceTree tree, List<String> pureMethods) throws IOException {

        try (ZipFile zip = new ZipFile(jmod.toFile())) {

            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();

                if (!e.getName().startsWith("classes/") || !e.getName().endsWith(".class"))
                    continue;

                try (InputStream is = zip.getInputStream(e)) {
                    ClassReader cr = new ClassReader(is);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

                    String className = cn.name.replace('/', '.');

                    boolean isInterface = (cn.access & Opcodes.ACC_INTERFACE) != 0;
                    boolean isAbstract = (cn.access & Opcodes.ACC_ABSTRACT) != 0 && !isInterface;

                    if (isInterface) {
                        tree.registerInterface(cn.name);
                    } else if (isAbstract) {
                        tree.registerAbstractClass(cn.name);
                    }

                    if (cn.superName != null)
                        tree.addSuperclass(cn.name, cn.superName, cn.access);

                    for (Object ifaceObj : cn.interfaces) {
                        String iface = ifaceObj.toString();
                        tree.addInterface(cn.name, iface);
                    }

                    @SuppressWarnings("unchecked")
                    List<MethodNode> methods = (List<MethodNode>) (List<?>) cn.methods;

                    for (MethodNode m : methods) {

                        if (!m.name.equals("<init>") && !m.name.equals("<clinit>")) {
                            pureMethods.add(formatPureMethod(className, m));
                        }

                        tree.addAnalyzedMethod(cn.name, m.name, m.desc);
                    }
                } catch (Throwable ignore) {}
            }
        }
    }

    private static String formatPureMethod(String className, MethodNode m) {
        Type mt = Type.getMethodType(m.desc);
        Type[] params = mt.getArgumentTypes();
        StringBuilder sb = new StringBuilder(className).append(".").append(m.name).append("(");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i].getClassName());
            if (i < params.length - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    private void writeInheritanceXml(InheritanceTree tree, Path output) throws IOException {
        XStream xs = new XStream();
        xs.addPermission(new AnyTypePermission());
        xs.allowTypesByWildcard(new String[]{"org.evosuite.**", "org.jgrapht.**"});

        try (BufferedWriter w = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            xs.toXML(tree, w);
        }
    }

    // =====================================================================================
    // SHADING
    // =====================================================================================

    private static class RelocationRule {
        final String pattern;
        final String replacement;
        final Set<String> excludes;

        RelocationRule(String pattern, String replacement) {
            this(pattern, replacement, Set.of());
        }

        RelocationRule(String pattern, String replacement, Set<String> excludes) {
            this.pattern = pattern;
            this.replacement = replacement;
            this.excludes = excludes;
        }

        boolean applies(String line) {
            return line.contains(pattern) && excludes.stream().noneMatch(line::contains);
        }

        String apply(String line) {
            if (applies(line)) {
                return line.replace(pattern, replacement);
            }
            return line;
        }
    }

    private static String applyShadeRules(String xml) {
        String shaded = xml;
        for (RelocationRule r : SHADE_RULES) {
            shaded = r.apply(shaded);
        }
        return shaded;
    }
}
