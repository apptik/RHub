package io.apptik.rhub.shield;


import io.apptik.rhub.RSHub;

/**
 * Helper Shield Maker based on Reactive Streams and fitting on {@link RSHub} instances
 */
public final class ShieldMakerRS {

    private final static ShieldMakerBase<RSHub> inst = new ShieldMakerBase<>();

    /**
     * Helper static method to generate Shield Instances
     * @param shieldClass class type of the shield interface
     * @param rsHub instance of RHub to fit the shield on
     * @param <T> Shield interface type
     * @return instance of the Shield
     */
    public static <T> T make(Class<T> shieldClass, RSHub rsHub) {
        return inst.make(shieldClass, rsHub, RSHub.class);
    }

}
