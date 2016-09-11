package io.apptik.rxhub;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ShieldMaker {

    public static <T> T make(Class<T> shieldClass, RxJava1Hub rxJava1Hub) {
        Constructor<T> constructor = getShieldImpl(shieldClass);

        if (constructor == null) {
            throw new RuntimeException("Unable to find implementation for " + shieldClass);
        }

        try {
            return constructor.newInstance(rxJava1Hub);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create shield instance.", cause);
        }
    }

    private static <T> Constructor<T> getShieldImpl(Class<T> shieldClass) {
        Constructor<T> shieldConstructor = null;

        String clsName = shieldClass.getName();

        try {
            Class<?> shieldImpl = Class.forName(clsName + "_Impl");
            //noinspection unchecked
            shieldConstructor = (Constructor<T>) shieldImpl.getConstructor(RxJava1Hub.class);
            System.out.println("Shield Impl found for: " +  shieldClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("Shield Impl Not found for: " + shieldClass.getName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        return shieldConstructor;
    }
}
