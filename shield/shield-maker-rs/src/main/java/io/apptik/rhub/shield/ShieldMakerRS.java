package io.apptik.rhub.shield;


import io.apptik.rhub.RSHub;

public class ShieldMakerRS {

    private static ShieldMakerBase<RSHub> inst = new ShieldMakerBase<>();

    public static <T> T make(Class<T> shieldClass, RSHub rsHub) {
        return inst.make(shieldClass, rsHub, RSHub.class);
    }

}
