package org.evosuite.utils;

import java.util.List;

public final class SafeClassFilters {

    private static final List<String> BLOCKED_PREFIXES = List.of(
        "sun.", "com.sun.", "jdk.internal.", "apple.",
        "java.awt.", "javax.swing.", "javafx.",

            //These were added for compatibility
            "org.hibernate.", "org.hsqldb.", "org.jboss.",
            "org.springframework.", "org.apache.commons.logging.", "javassist.", "antlr.", "org.dom4j.",
            "org.aopalliance.",
            "javax.servlet.",//note, Servlet is special. see comments in pom file
            "org.mockito.", "org.apache", "org.hamcrest", "org.objenesis"
    );

    public static boolean isBlocked(String className) {
        String name = className.replace('/', '.');
        for (String prefix : BLOCKED_PREFIXES) {
            if (name.startsWith(prefix)) return true;
        }
        return false;
    }
}
