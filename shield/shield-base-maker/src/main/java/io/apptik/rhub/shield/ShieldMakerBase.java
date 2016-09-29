package io.apptik.rhub.shield;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import io.apptik.rhub.RHub;

public class ShieldMakerBase<H extends RHub> {

    public <T> T make(Class<T> shieldClass, H rHub, Class<H> rHubClass) {
        Constructor<T> constructor = getShieldImpl(shieldClass, rHubClass);

        if (constructor == null) {
            throw new RuntimeException("Unable to find implementation for " + shieldClass);
        }

        try {
            return constructor.newInstance(rHub);
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

    private <T> Constructor<T> getShieldImpl(Class<T> shieldClass, Class<H> rHubClass) {
        Constructor<T> shieldConstructor = null;

        String clsName = shieldClass.getName();

        try {
            Class<?> shieldImpl = Class.forName(clsName + "_Impl");
            //noinspection unchecked
            shieldConstructor = (Constructor<T>) shieldImpl.getConstructor(rHubClass);
            System.out.println("Shield Impl found for: " + shieldClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("Shield Impl Not found for: " + shieldClass.getName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        return shieldConstructor;
    }
}
