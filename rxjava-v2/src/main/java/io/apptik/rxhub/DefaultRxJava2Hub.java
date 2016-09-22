package io.apptik.rxhub;

import static io.apptik.rxhub.RxJava2Hub.RxJava2PubProxyType.BehaviorSafeProxy;

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
