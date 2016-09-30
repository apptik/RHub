package io.apptik.rhub;


import static io.apptik.rhub.RxJava2ObsHub.RxJava2ObsProxyType.BehaviorObsSafeProxy;

public class DefaultRxJava2ObsHub extends AbstractRxJava2ObsHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return BehaviorObsSafeProxy;
    }

    @Override
    public boolean isProxyThreadsafe(Object tag) {
        return true;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
