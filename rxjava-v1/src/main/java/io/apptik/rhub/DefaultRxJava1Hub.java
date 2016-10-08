package io.apptik.rhub;

public class DefaultRxJava1Hub extends AbstractRxJava1Hub {

    @Override
    public RxJava1ProxyType getProxyType(Object tag) {
        return RxJava1ProxyType.SerializedBehaviorRelayProxy;
    }

    @Override
    public boolean canTriggerEmit(Object tag) {
        return true;
    }
}
