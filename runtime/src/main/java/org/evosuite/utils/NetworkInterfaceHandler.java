package org.evosuite.utils;

import org.evosuite.runtime.util.SafeReflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Optional;

public class NetworkInterfaceHandler implements InstantiationHandler {

    public NetworkInterfaceHandler() {}

    @Override
    public <T> Optional<T> tryInstantiate(Class<T> clazz) {

        if (!"java.net.NetworkInterface".equals(clazz.getName())) {
            return Optional.empty();
        }

        try {
            @SuppressWarnings("unchecked")
            Class<T> mock = (Class<T>)
                    Class.forName("org.evosuite.runtime.mock.java.net.MockNetworkInterface");

            // lookup with private access to the mock class
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(mock, MethodHandles.lookup());

            // find constructor: () -> new MockNetworkInterface()
            MethodHandle ctor = lookup.findConstructor(mock, SafeReflection.NOARGS_CONSTRUCTOR);

            @SuppressWarnings("unchecked")
            T instance = (T) ctor.invoke();

            return Optional.of(instance);

        } catch (ClassNotFoundException e) {
            return Optional.empty();
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
