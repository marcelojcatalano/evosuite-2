package org.evosuite.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public final class SafeReflection {

    public static Field findField(Class<?> clazz, String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            makeAccessible(f);
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    public static void makeAccessible(AccessibleObject obj) {
        try {
            if (!obj.canAccess(null)) {
                obj.setAccessible(true);
            }
        } catch (Exception ignored) {}
    }

    public static Object get(Field f, Object target) {
        try { return f.get(target); }
        catch (Exception e) { return null; }
    }

    public static void set(Field f, Object target, Object value) {
        try { f.set(target, value); }
        catch (Exception ignored) {}
    }
}
