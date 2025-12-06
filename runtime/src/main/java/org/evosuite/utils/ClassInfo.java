package org.evosuite.utils;

import java.util.List;

public class ClassInfo {

    public final String name;
    public final String superName;
    public final List<String> interfaces;
    public final List<String> methods;

    public ClassInfo(String name, String superName, List<String> interfaces, List<String> methods) {
        this.name = name;
        this.superName = superName;
        this.interfaces = interfaces;
        this.methods = methods;
    }
}
