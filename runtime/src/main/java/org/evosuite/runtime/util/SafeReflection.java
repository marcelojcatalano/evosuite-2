package org.evosuite.runtime.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public final class SafeReflection {

    public static final MethodType NOARGS_CONSTRUCTOR =
            MethodType.methodType(void.class);

    public static final MethodType METHOD_TYPE_VOID_NOARGS =
            MethodType.methodType(void.class);


    public static Field findField(Class<?> clazz, String name) {
      //  if (SafeClassFilters.isBlocked(clazz.getName())) return null;

        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object get(Field f, Object target) {
        if (f == null) return null;

       /* if (SafeClassFilters.isBlocked(f.getDeclaringClass().getName())) {
            return null; // no entrar a JDK bloqueda
        }*/

        try {
            VarHandle vh = lookupVarHandle(f);
            return vh.get(target);
        } catch (Throwable t) {
            return null;
        }
    }

    public static void set(Field f, Object target, Object value) {
        if (f == null) return;

        if (SafeClassFilters.isBlocked(f.getDeclaringClass().getName())) {
            return; // ignorar
        }

        try {
            VarHandle vh = lookupVarHandle(f);
            vh.set(target, value);
        } catch (Throwable ignored) {}
    }

    private static VarHandle lookupVarHandle(Field field) throws Throwable {
        Class<?> declaring = field.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaring, MethodHandles.lookup());
        return lookup.unreflectVarHandle(field);
    }
}
