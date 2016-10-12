package io.apptik.rhub.shield;

import io.apptik.rhub.RxJava2ObsHub;

/**
 * Helper Shield Maker based on RxJava 2.x and fitting on {@link RxJava2ObsHub} instances
 */
public final class ShieldMakerRxJava2 {

    private final static ShieldMakerBase<RxJava2ObsHub> inst = new ShieldMakerBase<>();

    /**
     * Helper static method to generate Shield Instances
     * @param shieldClass class type of the shield interface
     * @param rxJava2ObsHub instance of RHub to fit the shield on
     * @param <T> Shield interface type
     * @return instance of the Shield
     */
    public static <T> T make(Class<T> shieldClass, RxJava2ObsHub rxJava2ObsHub) {
        return inst.make(shieldClass, rxJava2ObsHub, RxJava2ObsHub.class);
    }

}
