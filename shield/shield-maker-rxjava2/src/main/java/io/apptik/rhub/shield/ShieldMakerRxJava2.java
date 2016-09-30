package io.apptik.rhub.shield;

import io.apptik.rhub.RxJava2ObsHub;

public class ShieldMakerRxJava2 {

    private static ShieldMakerBase<RxJava2ObsHub> inst = new ShieldMakerBase<>();

    public static <T> T make(Class<T> shieldClass, RxJava2ObsHub rxJava2ObsHub) {
        return inst.make(shieldClass, rxJava2ObsHub, RxJava2ObsHub.class);
    }

}
