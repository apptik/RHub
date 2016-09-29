package io.apptik.rhub;

import static io.apptik.rhub.RxJava2Hub.RxJava2PubProxyType.BehaviorSafeProxy;

public class DefaultRxJava2Hub extends AbstractRxJava2Hub {

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
