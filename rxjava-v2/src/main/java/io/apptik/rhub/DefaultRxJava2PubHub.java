package io.apptik.rhub;

import static io.apptik.rhub.RxJava2ProcProxyType.SafeBehaviorProcessorProxy;

public class DefaultRxJava2PubHub extends AbstractRxJava2PubHub {

    @Override
    public ProxyType getProxyType(Object tag) {
        return SafeBehaviorProcessorProxy;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
