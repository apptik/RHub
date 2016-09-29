package io.apptik.rhub.shield;

import io.apptik.rhub.RxJava1Hub;

public class ShieldMakerRxJava1 {

    private static ShieldMakerBase<RxJava1Hub> inst = new ShieldMakerBase<>();

    public static <T> T make(Class<T> shieldClass, RxJava1Hub rxJava1Hub) {
        return inst.make(shieldClass, rxJava1Hub, RxJava1Hub.class);
    }

}
