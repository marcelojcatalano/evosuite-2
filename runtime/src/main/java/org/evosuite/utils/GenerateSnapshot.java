package org.evosuite.utils;

import java.io.*;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class GenerateSnapshot {

    public static void main(String[] args) throws Exception {
        int javaVersion = Runtime.version().feature();
        System.out.println("Detected Java version: " + javaVersion);

        List<String> classNames = scanPublicJDKClasses();

        System.out.println("Total classes found: " + classNames.size());

        Map<String, ClassInfo> infoMap = analyzeInheritance(classNames);

        Path output = Paths.get("JDK_inheritance_" + javaVersion + ".xml");
        writeXML(infoMap, output);

        System.out.println("Snapshot generated: " + output.toAbsolutePath());
    }

    /** ---------------------------
     * 1) SCANEO DE TODAS LAS CLASES PÚBLICAS DE LA JDK
     * --------------------------- */
    private static List<String> scanPublicJDKClasses() {
        try {
            ModuleFinder finder = ModuleFinder.ofSystem();
            Set<ModuleReference> modules = finder.findAll()
                    .stream()
                    .filter(m -> m.descriptor().name().startsWith("java."))
                    .collect(Collectors.toSet());

            List<String> result = new ArrayList<>();

            for (ModuleReference module : modules) {
                module.open().list().forEach(entry -> {
                    if (entry.endsWith(".class")) {
                        String cls = entry
                                .replace('/', '.')
                                .replace(".class", "");

                        // ignoramos clases anónimas y synthetic
                        if (!cls.contains("$")) {
                            result.add(cls);
                        }
                    }
                });
            }

            return result;
        } catch (Throwable t) {
            throw new RuntimeException("Failed scanning module-path: " + t, t);
        }
    }

    /** ---------------------------
     * 2) ANALIZAR HERENCIA VIA REFLEXIÓN SEGURA
     * --------------------------- */
    private static Map<String, ClassInfo> analyzeInheritance(List<String> classNames) {
        Map<String, ClassInfo> map = new TreeMap<>();

        for (String name : classNames) {
            try {
                Class<?> cls = Class.forName(name, false, ClassLoader.getSystemClassLoader());

                ClassInfo info = new ClassInfo();
                info.name = cls.getName();

                if (cls.getSuperclass() != null) {
                    info.superClass = cls.getSuperclass().getName();
                }

                for (Class<?> iface : cls.getInterfaces()) {
                    info.interfaces.add(iface.getName());
                }

                map.put(info.name, info);

            } catch (Throwable ignore) {
                // algunas clases internas de la JDK no son cargables
            }
        }

        return map;
    }

    /** ---------------------------
     * 3) SERIALIZAR A XML
     * --------------------------- */
    private static void writeXML(Map<String, ClassInfo> infos, Path path) throws Exception {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = f.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("jdk");
        root.setAttribute("version", String.valueOf(Runtime.version().feature()));
        doc.appendChild(root);

        for (ClassInfo info : infos.values()) {
            Element cls = doc.createElement("class");
            cls.setAttribute("name", info.name);

            if (info.superClass != null) {
                Element sup = doc.createElement("super");
                sup.setTextContent(info.superClass);
                cls.appendChild(sup);
            }

            for (String iface : info.interfaces) {
                Element inf = doc.createElement("interface");
                inf.setTextContent(iface);
                cls.appendChild(inf);
            }

            root.appendChild(cls);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(path)));
    }

    /** ---------------------------
     * ESTRUCTURA DE DATOS
     * --------------------------- */
    private static class ClassInfo {
        String name;
        String superClass;
        List<String> interfaces = new ArrayList<>();
    }
}
