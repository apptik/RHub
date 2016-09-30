package io.apptik.rhub;

import static io.apptik.rhub.RxJava2ObsHub.RxJava2PubProxyType.BehaviorSafeProxy;

public class DefaultRxJava2PubHub extends AbstractRxJava2PubHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return BehaviorSafeProxy;
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
