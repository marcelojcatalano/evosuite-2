/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.classpath;

import org.evosuite.Properties;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * When running EvoSuite there are at least three different classpaths
 * to handle: (1) the one of the target project, (2) the one of EvoSuite
 * itself, and (3) the one of the starter of EvoSuite.
 * Note that (2) and (3) can indeed be different: eg if EvoSuite is
 * not run from command line, and rather run from Maven/Eclipse that
 * use their own classloaders.
 *
 * @author arcuri
 */
public class ClassPathHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathHandler.class);

    private static final ClassPathHandler singleton = new ClassPathHandler();


    /**
     * The classpath of the project to test, including all
     * its dependencies
     */
    private String targetClassPath;

    /**
     * When we start the client, we need to know what is the classpath of EvoSuite.
     * Problem is, if EvoSuite was called from Eclipse/Maven, when the classpath will
     * not be part of the system classpath. And might not be feasible to query the
     * current classloader to get such info, as we cannot make any assumption on its implementation.
     * So, in those cases, we need to manually specify it
     */
    private String evosuiteClassPath;


    private ClassPathHandler() {
    }

    public static ClassPathHandler getInstance() {
        return singleton;
    }

    public static void resetSingleton() {
        getInstance().targetClassPath = null;
        getInstance().evosuiteClassPath = null;
    }

    public String getEvoSuiteClassPath() {
        if (evosuiteClassPath == null) {
            evosuiteClassPath = System.getProperty("java.class.path");
        }

        return evosuiteClassPath;
    }

    /**
     * Replace current CP of EvoSuite with the given <code>elements</code>
     *
     * @param elements
     * @throws IllegalArgumentException if values in <code>elements</code> are not valid classpath entries
     */
    public void setEvoSuiteClassPath(String[] elements) throws IllegalArgumentException {
        String cp = getClassPath(elements);
        evosuiteClassPath = cp;
    }


    /**
     * Replace current CP for target project with the given <code>elements</code>
     *
     * @param elements
     * @throws IllegalArgumentException if values in <code>elements</code> are not valid classpath entries
     */
    public void changeTargetClassPath(String[] elements) throws IllegalArgumentException {
        String cp = getClassPath(elements);
        Properties.CP = cp;
        targetClassPath = cp;
    }

    private String getClassPath(String[] elements) {
        if (elements == null || elements.length == 0) {
            throw new IllegalArgumentException("No classpath elements");
        }

        return Arrays.stream(elements)
                .peek(this::checkIfValidClasspathEntry)
                .collect(Collectors.joining(File.pathSeparator));
    }


    public String getTargetProjectClasspath() {

        if (targetClassPath == null) {
            String line = null;

            if (Properties.CP_FILE_PATH != null) {
                Path path = Paths.get(Properties.CP_FILE_PATH);

                if (Files.exists(path)) {
                    try (Scanner scanner = new Scanner(Files.newBufferedReader(path))) {
                        if (scanner.hasNextLine()) {
                            line = scanner.nextLine();
                        }
                    } catch (Exception e) {
                        LoggingUtils.getEvoLogger().error(
                                "Error while processing " + path.toAbsolutePath() + " : " + e.getMessage()
                        );
                    }
                }
            }

            targetClassPath = (line != null) ? line : Properties.CP;
        }

        return targetClassPath;
    }


    public static String writeClasspathToFile(String classpath) {

        try {
            File file = File.createTempFile("EvoSuite_classpathFile", ".txt");
            file.deleteOnExit();

            try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(classpath);
                out.newLine();
            }

            return file.getAbsolutePath();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create tmp file for classpath specification: " + e.getMessage());
        }
    }


    public void addElementToTargetProjectClassPath(String element) {
        checkIfValidClasspathEntry(element);

        String current = getTargetProjectClasspath();

        if (current == null || current.isEmpty()) {
            targetClassPath = element;
        } else {
            // Si ya está, no dupliques
            String[] parts = current.split(File.pathSeparator);
            boolean alreadyPresent = Arrays.asList(parts).contains(element);

            if (alreadyPresent) {
                return;
            }

            // Agregar al classpath de forma correcta
            targetClassPath = current + File.pathSeparator + element;
        }

        // sincronizar con Properties
        Properties.CP = targetClassPath;
    }


    private void checkIfValidClasspathEntry(String element) {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("Empty input element");
        }

        Path path = Paths.get(element);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Classpath element does not exist on disk at: " + element);
        }

        if (Files.isDirectory(path)) {
            // Directorio → válido
            return;
        }

        if (Files.isRegularFile(path) && !element.endsWith(".jar")) {
            throw new IllegalArgumentException(
                    "A classpath element should either be a jar or a folder: " + element
            );
        }
    }


    /**
     * Get the project classpath as an array of elements
     *
     * @return a non-null array
     */
    public String[] getClassPathElementsForTargetProject() {
        String cp = getTargetProjectClasspath();
        if (cp == null) {
            return new String[0];
        }
        return cp.split(File.pathSeparator);
    }

    /**
     * This is meant only for running the EvoSuite test cases, whose CUTs will be in the
     * classpath of EvoSuite itself
     */
    public void changeTargetCPtoTheSameAsEvoSuite() {
        Path outDir = Paths.get("target", "classes");

        if (Files.exists(outDir)) {
            changeTargetClassPath(new String[]{ outDir.toAbsolutePath().toString() });

            Path testDir = Paths.get("target", "test-classes");
            if (Files.exists(testDir)) {
                addElementToTargetProjectClassPath(testDir.toAbsolutePath().toString());
            }

        } else {
            // fallback
            changeTargetClassPath(getEvoSuiteClassPath().split(File.pathSeparator));
        }
    }
}