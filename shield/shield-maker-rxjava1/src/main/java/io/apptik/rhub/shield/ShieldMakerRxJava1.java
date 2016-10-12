package io.apptik.rhub.shield;

import io.apptik.rhub.RxJava1Hub;


/**
 * Helper Shield Maker based on RxJava 1.x and fitting on {@link RxJava1Hub} instances
 */
public final class ShieldMakerRxJava1 {

    private final static ShieldMakerBase<RxJava1Hub> inst = new ShieldMakerBase<>();

    /**
     * Helper static method to generate Shield Instances
     * @param shieldClass class type of the shield interface
     * @param rxJava1Hub instance of RHub to fit the shield on
     * @param <T> Shield interface type
     * @return instance of the Shield
     */
    public static <T> T make(Class<T> shieldClass, RxJava1Hub rxJava1Hub) {
        return inst.make(shieldClass, rxJava1Hub, RxJava1Hub.class);
    }

}
